package com.redhawk.wallet.feature_auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthManager {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun isEmailVerified(): Boolean = firebaseAuth.currentUser?.isEmailVerified ?: false

    suspend fun reloadUser() {
        firebaseAuth.currentUser?.reload()?.await()
    }

    suspend fun register(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid
            if (uid.isNullOrBlank()) {
                Result.failure(Exception("Registration succeeded but UID is missing"))
            } else {
                Result.success(uid)
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

            val uid = result.user?.uid
            if (uid.isNullOrBlank()) {
                Result.failure(Exception("Login succeeded but UID is missing"))
            } else {
                Result.success(uid)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("No logged in user found"))
            } else {
                user.sendEmailVerification().await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}