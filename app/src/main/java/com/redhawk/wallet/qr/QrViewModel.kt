package com.redhawk.wallet.qr

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redhawk.wallet.data.models.UserProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QrViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    var userProfile by mutableStateOf(UserProfile())
        private set

    var qrBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadStudentProfile() {
        val currentUser = auth.currentUser ?: run {
            userProfile = UserProfile(
                uid = "",
                name = "Not logged in",
                email = "",
                studentId = "",
                photoUrl = null,
                role = "student",
                isEmailVerified = false,
                createdAt = 0L
            )
            errorMessage = "No logged-in user found."
            return
        }

        viewModelScope.launch {
            try {
                val uid = currentUser.uid
                val doc = db.collection("users")
                    .document(uid)
                    .get()
                    .await()

                if (doc.exists()) {
                    userProfile = UserProfile(
                        uid = uid,
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: (currentUser.email ?: ""),
                        studentId = doc.getString("studentId") ?: "",
                        photoUrl = doc.getString("photoUrl"),
                        role = doc.getString("role") ?: "student",
                        isEmailVerified = doc.getBoolean("isEmailVerified")
                            ?: currentUser.isEmailVerified,
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                    errorMessage = null
                } else {
                    userProfile = UserProfile(
                        uid = uid,
                        name = currentUser.displayName ?: "",
                        email = currentUser.email ?: "",
                        studentId = "",
                        photoUrl = null,
                        role = "student",
                        isEmailVerified = currentUser.isEmailVerified,
                        createdAt = 0L
                    )
                    errorMessage = "No Firestore profile found."
                }
            } catch (e: Exception) {
                Log.e("QrViewModel", "Failed to load profile", e)
                userProfile = UserProfile(
                    uid = currentUser.uid,
                    name = currentUser.displayName ?: "",
                    email = currentUser.email ?: "",
                    studentId = "",
                    photoUrl = null,
                    role = "student",
                    isEmailVerified = currentUser.isEmailVerified,
                    createdAt = 0L
                )
                errorMessage = e.message ?: "Failed to load Firestore profile."
            }
        }
    }

    fun uploadProfilePhoto(uri: Uri) {
        val uid = auth.currentUser?.uid ?: run {
            errorMessage = "No logged-in user found."
            return
        }

        viewModelScope.launch {
            try {
                errorMessage = null

                val photoRef = storage.reference.child("profile_photos/$uid.jpg")

                photoRef.putFile(uri).await()
                val downloadUrl = photoRef.downloadUrl.await().toString()

                db.collection("users")
                    .document(uid)
                    .update("photoUrl", downloadUrl)
                    .await()

                userProfile = userProfile.copy(photoUrl = downloadUrl)
            } catch (e: Exception) {
                Log.e("QrViewModel", "Photo upload failed", e)
                errorMessage = e.message ?: "Photo upload failed."
            }
        }
    }

    fun generateQrIfNeeded() {
        if (qrBitmap != null) return

        val uid = userProfile.uid
        val studentId = userProfile.studentId

        if (uid.isBlank()) {
            errorMessage = "Cannot generate QR without a valid user."
            return
        }

        val payload = "MSU|$uid|$studentId"

        try {
            qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
            errorMessage = null
        } catch (e: Exception) {
            Log.e("QrViewModel", "Failed to generate QR", e)
            qrBitmap = null
            errorMessage = "QR generation failed."
        }
    }

    fun forceRefreshQr() {
        qrBitmap = null
        generateQrIfNeeded()
    }
}