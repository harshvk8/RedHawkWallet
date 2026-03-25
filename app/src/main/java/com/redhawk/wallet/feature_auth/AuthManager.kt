package com.redhawk.wallet.feature_auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthManager {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

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
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}