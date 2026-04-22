package com.redhawk.wallet.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TapUiState(
    val balanceText: String = "Balance: --",
    val transactionsText: String = "",
    val error: String? = null,
    val loading: Boolean = false,
    val isEmailVerified: Boolean = false
)

class TapToPayViewModel(
    private val walletRepo: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TapUiState())
    val state: StateFlow<TapUiState> = _state

    private fun uid(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    private suspend fun checkEmailVerified(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()
        return user?.isEmailVerified ?: false
    }

    fun loadDashboard() {
        val u = uid()
        if (u.isBlank()) return

        viewModelScope.launch {
            try {
                val emailVerified = checkEmailVerified()

                var wallet = walletRepo.getWallet(u)
                if (wallet == null) {
                    walletRepo.initWallet(u)
                    wallet = walletRepo.getWallet(u)
                }

                val txs = walletRepo.getLatestTransactions(u)

                _state.value = _state.value.copy(
                    balanceText = "Balance: $${wallet?.balance ?: 0.0}",
                    transactionsText = txs.joinToString("\n") { tx ->
                        "• -$${tx.amount} | ${tx.status} | ${tx.token.take(8)}..."
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
                    error = e.message ?: "Failed to load wallet/transactions"
                )
            }
        }
    }

    fun simulateTap() {
        val u = uid()
        if (u.isBlank()) return

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
                    walletRepo.initWallet(u)
                }

                walletRepo.tapAndPay(u)
                loadDashboard()

                _state.value = _state.value.copy(loading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Tap failed"
                )
            }
        }
    }
}

class TapToPayViewModelFactory(
    private val walletRepo: WalletRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TapToPayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TapToPayViewModel(walletRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}