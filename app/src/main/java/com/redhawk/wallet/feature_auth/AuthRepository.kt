package com.redhawk.wallet.feature_auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val authManager: AuthManager,
    private val sessionManager: SessionManager
) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    suspend fun register(email: String, password: String): Result<String> {
        return try {

            authManager.signOut()
            sessionManager.logout()


            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = result.user

            if (user != null) {

                user.sendEmailVerification().await()


                sessionManager.setLoggedIn(true)
                sessionManager.setUserId(user.uid)

                Result.success(user.uid)
            } else {
                Result.failure(Exception("User creation failed"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = result.user

            if (user != null) {
                sessionManager.setLoggedIn(true)
                sessionManager.setUserId(user.uid)

                Result.success(user.uid)
            } else {
                Result.failure(Exception("Login failed"))
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