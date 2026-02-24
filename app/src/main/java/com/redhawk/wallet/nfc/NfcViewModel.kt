package com.redhawk.wallet.nfc

import android.content.Intent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NfcUiState(
    val message: String = "NFC: Waiting...",
    val lastToken: String? = null
)

class NfcViewModel : ViewModel() {

    private val reader = NfcReader()

    private val _uiState = MutableStateFlow(NfcUiState())
    val uiState: StateFlow<NfcUiState> = _uiState.asStateFlow()

    fun setDeviceStatus(isSupported: Boolean, isEnabled: Boolean) {
        _uiState.value = when {
            !isSupported -> NfcUiState(message = "NFC: Not supported on this device")
            !isEnabled -> NfcUiState(message = "NFC: Supported but OFF (turn it ON in settings)")
            else -> NfcUiState(message = "NFC: Ready — tap a tag/card")
        }
    }

    /**
     * Call this from MainActivity.onNewIntent(intent)
     */
    fun onNfcIntent(intent: Intent?) {
        val result = reader.readFromIntent(intent)

        _uiState.value = when (result) {
            is NfcResult.Success -> {
                // ✅ This is where you call repository/DB later:
                // repository.saveTransaction(result.token)
                NfcUiState(
                    message = "NFC Token: ${result.token}",
                    lastToken = result.token
                )
            }
            is NfcResult.Error -> NfcUiState(message = "NFC Error: ${result.message}")
            NfcResult.Disabled -> NfcUiState(message = "NFC is OFF. Turn it ON in settings.")
            NfcResult.NotSupported -> NfcUiState(message = "NFC not supported.")
        }
    }
}
