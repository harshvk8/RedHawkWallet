package com.redhawk.wallet.feature_auth

class AuthRepository(
    private val authManager: AuthManager = AuthManager(),
    private val sessionManager: SessionManager
) {


    fun signUp(
        universityId: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        // Clear any old Firebase/local session before new registration
        clearAllUserData()

        authManager.signUpWithUniversityId(universityId, password) { result ->
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
        universityId: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        authManager.signInWithUniversityId(universityId, password) { result ->
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
        authManager.signOut()
        sessionManager.clearSession()
    }

    fun getCurrentUser() = authManager.getCurrentUser()

    fun getCurrentUniversityId(): String? {
        return authManager.getCurrentUniversityId()
    }

    fun isUserLoggedIn(): Boolean {
        return authManager.isUserLoggedIn()
    }
}
