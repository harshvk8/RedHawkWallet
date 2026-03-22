package com.redhawk.wallet.feature_auth

class AuthRepository(
    private val authManager: AuthManager = AuthManager(),
    private val sessionManager: SessionManager
) {

    fun signUp(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        // 🔥 CRITICAL FIX: clear old user/session BEFORE registering
        clearAllUserData()

        authManager.signUp(email, password) { result ->
            if (result is AuthResult.Success) {
                sessionManager.setLoggedIn(true)

                val isVerified =
                    authManager.getCurrentUser()?.isEmailVerified ?: false
                sessionManager.setEmailVerified(isVerified)
            }
            onResult(result)
        }
    }

    fun signIn(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        authManager.signIn(email, password) { result ->
            if (result is AuthResult.Success) {
                sessionManager.setLoggedIn(true)

                val isVerified =
                    authManager.getCurrentUser()?.isEmailVerified ?: false
                sessionManager.setEmailVerified(isVerified)
            }
            onResult(result)
        }
    }

    fun signOut() {
        clearAllUserData()
    }


    private fun clearAllUserData() {
        authManager.signOut()        // Firebase logout
        sessionManager.clearSession() // Local storage clear
    }

    fun getCurrentUser() = authManager.getCurrentUser()

    fun isUserLoggedIn(): Boolean {
        return authManager.getCurrentUser() != null
    }
}