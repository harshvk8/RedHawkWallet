package com.redhawk.wallet.feature_auth

class AuthRepository(
    private val authManager: AuthManager = AuthManager()
) {

    fun signUp(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        authManager.signUp(email, password, onResult)
    }

    fun signIn(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        authManager.signIn(email, password, onResult)
    }

    fun signOut() {
        authManager.signOut()
    }

    fun getCurrentUser() = authManager.getCurrentUser()
}
