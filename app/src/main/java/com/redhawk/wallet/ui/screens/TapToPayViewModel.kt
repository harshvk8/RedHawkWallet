package com.redhawk.wallet.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.models.AccountType
import com.redhawk.wallet.data.models.SelectedAccountStore
import com.redhawk.wallet.data.models.Wallet
import com.redhawk.wallet.data.repository.WalletRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TapUiState(
    val selectedAccount: AccountType = AccountType.RED_HAWK_DOLLARS,
    val balanceText: String = "Balance: --",
    val transactionsText: String = "",
    val error: String? = null,
    val loading: Boolean = false,
    val isEmailVerified: Boolean = false
)

class TapToPayViewModel(
    application: Application,
    private val walletRepo: WalletRepository
) : AndroidViewModel(application) {

    private val accountStore = SelectedAccountStore(application)

    private val _state = MutableStateFlow(
        TapUiState(selectedAccount = accountStore.get())
    )
    val state: StateFlow<TapUiState> = _state

    private var refreshJob: Job? = null

    private fun uid(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    private suspend fun checkEmailVerified(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()
        return user?.isEmailVerified ?: false
    }

    fun selectAccount(accountType: AccountType) {
        accountStore.save(accountType)
        _state.value = _state.value.copy(selectedAccount = accountType)
        loadDashboard()
    }

    fun loadDashboard() {
        val u = uid()
        if (u.isBlank()) {
            _state.value = _state.value.copy(error = "User not logged in.")
            return
        }

        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            refreshDashboardState(u)
        }
    }

    private suspend fun refreshDashboardState(uid: String) {
        try {
            _state.value = _state.value.copy(loading = true, error = null)

            val emailVerified = checkEmailVerified()
            val wallet = walletRepo.getWallet(uid)

            if (wallet == null) {
                _state.value = _state.value.copy(
                    loading = false,
                    isEmailVerified = emailVerified,
                    error = "Wallet not found for this user."
                )
                return
            }

            val selectedAccount = accountStore.get()
            val selectedBalance = getSelectedBalance(wallet, selectedAccount)
            val txs = walletRepo.getLatestTransactions(uid)

            val formattedBalance = when (selectedAccount) {
                AccountType.MEAL_SWIPES -> "${selectedBalance.toInt()} left"
                else -> "$$selectedBalance"
            }

            _state.value = _state.value.copy(
                selectedAccount = selectedAccount,
                loading = false,
                balanceText = formattedBalance,
                transactionsText = txs.joinToString("\n") { tx ->
                    val amountText = if (tx.type == AccountType.MEAL_SWIPES.name.lowercase()) {
                        "-${tx.amount.toInt()} swipe"
                    } else {
                        "-$${tx.amount}"
                    }
                    "• $amountText | ${tx.status} | ${tx.token.take(8)}..."
                },
                isEmailVerified = emailVerified,
                error = if (!emailVerified) {
                    "⚠️ Please verify your email to use payments."
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                loading = false,
                error = e.message ?: "Failed to load wallet/transactions"
            )
        }
    }

    fun simulateTap() {
        val u = uid()
        if (u.isBlank()) {
            _state.value = _state.value.copy(error = "User not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                val emailVerified = checkEmailVerified()

                if (!emailVerified) {
                    _state.value = _state.value.copy(
                        error = "⚠️ Please verify your email before making payments."
                    )
                    return@launch
                }

                _state.value = _state.value.copy(
                    loading = true,
                    error = null
                )

                val wallet = walletRepo.getWallet(u)
                if (wallet == null) {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = "Wallet not found for this user."
                    )
                    return@launch
                }

                val selectedAccount = accountStore.get()
                walletRepo.tapAndPay(u, selectedAccount)

                refreshDashboardState(u)

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Tap failed"
                )
            }
        }
    }

    private fun getSelectedBalance(wallet: Wallet, accountType: AccountType): Double {
        return when (accountType) {
            AccountType.RED_HAWK_DOLLARS -> wallet.redHawkDollars
            AccountType.FLEX -> wallet.flex
            AccountType.BONUS -> wallet.bonus
            AccountType.MEAL_SWIPES -> wallet.mealSwipes
        }
    }
}

class TapToPayViewModelFactory(
    private val application: Application,
    private val walletRepo: WalletRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TapToPayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TapToPayViewModel(application, walletRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}