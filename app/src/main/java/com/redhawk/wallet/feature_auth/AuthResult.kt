package com.redhawk.wallet.feature_auth

sealed class AuthResult {

    object Success : AuthResult()

    data class Error(
        val message: String
    ) : AuthResult()

    object Loading : AuthResult()
}
