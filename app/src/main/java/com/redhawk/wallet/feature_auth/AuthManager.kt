package com.redhawk.wallet.feature_auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthManager {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val UNIVERSITY_AUTH_DOMAIN = "redhawkwallet.edu"
    }

    /**
     * Convert one shared university ID into a Firebase-friendly email.
     * Example: 12345678 -> 12345678@redhawkwallet.edu
     */
    private fun universityIdToEmail(universityId: String): String {
        val cleanedId = universityId.trim().lowercase()
        return "$cleanedId@$UNIVERSITY_AUTH_DOMAIN"
    }

    /**
     * Optional validation for the university ID.
     * Change the regex if your school has a different ID format.
     */
    fun isValidUniversityId(universityId: String): Boolean {
        val cleanedId = universityId.trim()
        return cleanedId.matches(Regex("^[a-zA-Z0-9]{5,20}$"))
    }

    /**
     * Sign up using a single university ID + password flow
     * for both students and professors.
     */
    fun signUpWithUniversityId(
        universityId: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        if (!isValidUniversityId(universityId)) {
            onResult(AuthResult.Error("Invalid university ID"))
            return
        }

        val authEmail = universityIdToEmail(universityId)

        firebaseAuth.createUserWithEmailAndPassword(authEmail, password)
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
     * Sign in using the same shared university ID + password flow.
     */
    fun signInWithUniversityId(
        universityId: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        if (!isValidUniversityId(universityId)) {
            onResult(AuthResult.Error("Invalid university ID"))
            return
        }

        val authEmail = universityIdToEmail(universityId)

        firebaseAuth.signInWithEmailAndPassword(authEmail, password)
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

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Helpful if you want the app to display the user's university ID
     * instead of the generated Firebase email.
     */
    fun getCurrentUniversityId(): String? {
        val email = firebaseAuth.currentUser?.email ?: return null
        return if (email.endsWith("@$UNIVERSITY_AUTH_DOMAIN")) {
            email.substringBefore("@")
        } else {
            null
        }
    }
}