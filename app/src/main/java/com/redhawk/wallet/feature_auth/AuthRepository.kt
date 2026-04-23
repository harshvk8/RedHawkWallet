package com.redhawk.wallet.feature_auth

class AuthRepository(
    private val authManager: AuthManager,
    private val sessionManager: SessionManager
) {

    suspend fun register(email: String, password: String): Result<String> {
        return try {
            authManager.signOut()
            sessionManager.logout()

            val result = authManager.register(email, password)

            if (result.isSuccess) {
                val uid = result.getOrNull().orEmpty()

                if (uid.isBlank()) {
                    Result.failure(Exception("User creation failed"))
                } else {
                    val verificationResult = authManager.sendEmailVerification()
                    if (verificationResult.isFailure) {
                        Result.failure(
                            verificationResult.exceptionOrNull()
                                ?: Exception("Failed to send verification email")
                        )
                    } else {
                        sessionManager.setLoggedIn(true)
                        sessionManager.setUserId(uid)
                        Result.success(uid)
                    }
                }
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = authManager.login(email, password)

            if (result.isSuccess) {
                val uid = result.getOrNull().orEmpty()

                if (uid.isBlank()) {
                    Result.failure(Exception("Login failed"))
                } else {
                    sessionManager.setLoggedIn(true)
                    sessionManager.setUserId(uid)
                    Result.success(uid)
                }
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        authManager.signOut()
        sessionManager.logout()
    }
}