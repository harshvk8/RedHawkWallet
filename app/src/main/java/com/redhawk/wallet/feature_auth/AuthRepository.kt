package com.redhawk.wallet.feature_auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerIfAllowed(
        name: String,
        studentId: String,
        email: String,
        password: String
    ): AuthResult {
        return try {
            Log.d("AUTH", "Checking allowed_users in Firestore...")

            val allowedDoc = db.collection("allowed_users")
                .document(email)
                .get()
                .await()

            Log.d("AUTH", "Allowed doc exists = ${allowedDoc.exists()}")

            if (!allowedDoc.exists()) {
                return AuthResult.Error("You are not allowed to register. Contact admin.")
            }

            val enabled = allowedDoc.getBoolean("enabled") ?: false
            if (!enabled) {
                return AuthResult.Error("Your account is disabled. Contact admin.")
            }

            val allowedStudentId = allowedDoc.getString("studentId") ?: ""
            if (allowedStudentId != studentId) {
                return AuthResult.Error("Student ID does not match records.")
            }

            Log.d("AUTH", "Creating Firebase Auth user...")

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Registration failed")
            val uid = user.uid

            Log.d("AUTH", "Auth created UID = $uid")

            // ✅ Send verification email
            user.sendEmailVerification().await()
            Log.d("AUTH", "Verification email sent to $email")

            val profile = hashMapOf(
                "uid" to uid,
                "name" to name,
                "studentId" to studentId,
                "email" to email,
                "isEmailVerified" to false
            )

            Log.d("AUTH", "Saving profile to Firestore...")

            db.collection("users")
                .document(uid)
                .set(profile)
                .await()

            Log.d("AUTH", "Profile saved successfully")

            AuthResult.VerificationSent(uid)

        } catch (e: Exception) {
            Log.e("AUTH", "Register failed", e)
            AuthResult.Error(e.message ?: "Registration error")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            Log.d("AUTH", "Logging in...")

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Login failed")
            val uid = user.uid

            // ✅ Check if email is verified
            if (!user.isEmailVerified) {
                Log.d("AUTH", "Email not verified for UID = $uid")
                return AuthResult.EmailNotVerified(uid)
            }

            Log.d("AUTH", "Login success UID = $uid")
            AuthResult.Success(uid)

        } catch (e: Exception) {
            Log.e("AUTH", "Login failed", e)
            AuthResult.Error(e.message ?: "Login error")
        }
    }

    suspend fun resendVerificationEmail(): AuthResult {
        return try {
            val user = auth.currentUser
                ?: return AuthResult.Error("No user logged in")
            user.sendEmailVerification().await()
            Log.d("AUTH", "Verification email resent")
            AuthResult.VerificationSent(user.uid)
        } catch (e: Exception) {
            Log.e("AUTH", "Resend failed", e)
            AuthResult.Error(e.message ?: "Failed to resend email")
        }
    }

    suspend fun checkEmailVerified(): Boolean {
        return try {
            auth.currentUser?.reload()?.await()
            auth.currentUser?.isEmailVerified ?: false
        } catch (e: Exception) {
            false
        }
    }
}