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


    fun signOut() {
        firebaseAuth.signOut()
    }
}