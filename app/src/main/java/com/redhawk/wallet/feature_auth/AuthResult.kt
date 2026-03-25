package com.redhawk.wallet.feature_auth

sealed class AuthResult {
    data class Success(val uid: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data class VerificationSent(val uid: String) : AuthResult()
    data class EmailNotVerified(val uid: String) : AuthResult()
    object Loading : AuthResult()
}