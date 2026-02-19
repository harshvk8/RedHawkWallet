package com.redhawk.wallet.feature_auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthManager {

    // Firebase instance
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Sign up a new user with email & password
     */
    fun signUp(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(AuthResult.Success)
                } else {
                    onResult(
                        AuthResult.Error(
                            task.exception?.message ?: "Signup failed"
                        )
                    )
                }
            }
    }

    /**
     * Sign in existing user
     */
    fun signIn(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(AuthResult.Success)
                } else {
                    onResult(
                        AuthResult.Error(
                            task.exception?.message ?: "Login failed"
                        )
                    )
                }
            }
    }

    /**
     * Logout current user
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Get currently logged in user
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}
