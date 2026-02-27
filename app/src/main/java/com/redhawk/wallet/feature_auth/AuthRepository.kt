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
            val uid = result.user?.uid ?: return AuthResult.Error("Registration failed")

            Log.d("AUTH", "Auth created UID = $uid")

            val profile = hashMapOf(
                "uid" to uid,
                "name" to name,
                "studentId" to studentId,
                "email" to email
            )

            Log.d("AUTH", "Saving profile to Firestore...")

            db.collection("users")
                .document(uid)
                .set(profile)
                .await()

            Log.d("AUTH", "Profile saved successfully")

            AuthResult.Success(uid)

        } catch (e: Exception) {
            Log.e("AUTH", "Register failed", e)
            AuthResult.Error(e.message ?: "Registration error")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            Log.d("AUTH", "Logging in...")

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Login failed")

            Log.d("AUTH", "Login success UID = $uid")
            AuthResult.Success(uid)

        } catch (e: Exception) {
            Log.e("AUTH", "Login failed", e)
            AuthResult.Error(e.message ?: "Login error")
        }
    }
}