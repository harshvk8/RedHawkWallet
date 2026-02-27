package com.redhawk.wallet.ui.screens

import androidx.lifecycle.ViewModel
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
    val loading: Boolean = false
)

class TapToPayViewModel(
    private val walletRepo: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TapUiState())
    val state: StateFlow<TapUiState> = _state

    private fun uid(): String =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun loadDashboard() {
        val u = uid()
        if (u.isBlank()) return

        viewModelScope.launch {
            try {
                // ✅ ALWAYS ensure wallet exists
                var w = walletRepo.getWallet(u)
                if (w == null) {
                    walletRepo.initWallet(u)        // creates 200
                    w = walletRepo.getWallet(u)
                }

                val txs = walletRepo.getLatestTransactions(u)

                _state.value = _state.value.copy(
                    balanceText = "Balance: $${w?.balance ?: 0.0}",
                    transactionsText = txs.joinToString("\n") {
                        "• -$${it.amount} | ${it.status} | ${it.token.take(8)}..."
                    },
                    error = null
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
                _state.value = _state.value.copy(loading = true, error = null)

                // ✅ ensure wallet exists before deduct
                val w = walletRepo.getWallet(u)
                if (w == null) {
                    walletRepo.initWallet(u)  // creates 200
                }

                walletRepo.tapAndPay(u)  // deduct 5 + save tx
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