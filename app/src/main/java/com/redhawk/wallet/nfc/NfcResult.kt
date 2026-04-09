package com.redhawk.wallet.nfc

sealed class NfcResult {
    data class Success(val token: String) : NfcResult()
    data class Error(val message: String) : NfcResult()
    data object Disabled : NfcResult()
    data object NotSupported : NfcResult()
}
