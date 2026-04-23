package com.redhawk.wallet.qr

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.redhawk.wallet.data.models.UserProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class VerificationUi(
    val isValid: Boolean,
    val title: String,
    val name: String,
    val role: String,
    val idLabel: String,
    val idValue: String,
    val email: String,
    val message: String
)

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

    var verificationUi by mutableStateOf<VerificationUi?>(null)
        private set

    private fun parseCreatedAt(doc: DocumentSnapshot): Long {
        val value = doc.get("createdAt")
        return when (value) {
            is Timestamp -> value.toDate().time
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is Float -> value.toLong()
            is Number -> value.toLong()
            else -> 0L
        }
    }

    fun loadUserProfile() {
        val currentUser = auth.currentUser ?: run {
            userProfile = UserProfile(
                uid = "",
                name = "Not logged in",
                email = "",
                universityId = "",
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
                    val role = (doc.getString("role") ?: "student").lowercase()

                    val resolvedUniversityId =
                        doc.getString("universityId")
                            ?: doc.getString("studentId")
                            ?: ""

                    userProfile = UserProfile(
                        uid = uid,
                        name = doc.getString("name") ?: currentUser.displayName.orEmpty(),
                        email = doc.getString("email") ?: currentUser.email.orEmpty(),
                        universityId = resolvedUniversityId,
                        photoUrl = doc.getString("photoUrl"),
                        role = role,
                        isEmailVerified = doc.getBoolean("isEmailVerified")
                            ?: currentUser.isEmailVerified,
                        createdAt = parseCreatedAt(doc)
                    )
                    errorMessage = null
                } else {
                    userProfile = UserProfile(
                        uid = uid,
                        name = currentUser.displayName ?: "",
                        email = currentUser.email ?: "",
                        universityId = "",
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
                    universityId = "",
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
        val universityId = userProfile.universityId

        if (uid.isBlank()) {
            errorMessage = "Cannot generate QR without a valid user."
            return
        }

        if (universityId.isBlank()) {
            errorMessage = "Cannot generate QR without a university ID."
            return
        }

        try {
            val payload = "MSU|$uid|$universityId"
            qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
            errorMessage = null
        } catch (e: Exception) {
            Log.e("QrViewModel", "Failed to generate QR", e)
            qrBitmap = null
            errorMessage = e.message ?: "QR generation failed."
        }
    }

    fun forceRefreshQr() {
        qrBitmap = null
        generateQrIfNeeded()
    }

    fun verifyScannedQr(rawValue: String) {
        if (rawValue.isBlank()) {
            verificationUi = VerificationUi(
                isValid = false,
                title = "Invalid QR",
                name = "",
                role = "",
                idLabel = "",
                idValue = "",
                email = "",
                message = "Scanned QR is empty."
            )
            return
        }

        val parts = rawValue.split("|")
        if (parts.size < 3 || parts[0] != "MSU") {
            verificationUi = VerificationUi(
                isValid = false,
                title = "Invalid QR",
                name = "",
                role = "",
                idLabel = "",
                idValue = "",
                email = "",
                message = "This is not a valid Montclair QR code."
            )
            return
        }

        val uid = parts[1]
        val qrUniversityId = parts[2]

        viewModelScope.launch {
            try {
                val doc = db.collection("users")
                    .document(uid)
                    .get()
                    .await()

                if (!doc.exists()) {
                    verificationUi = VerificationUi(
                        isValid = false,
                        title = "User Not Found",
                        name = "",
                        role = "",
                        idLabel = "",
                        idValue = "",
                        email = "",
                        message = "No user record found for this QR."
                    )
                    return@launch
                }

                val name = doc.getString("name") ?: "Unknown User"
                val email = doc.getString("email") ?: ""
                val role = (doc.getString("role") ?: "student").lowercase()
                val universityId =
                    doc.getString("universityId")
                        ?: doc.getString("studentId")
                        ?: ""

                val scannedUser = auth.currentUser
                val firebaseVerified =
                    scannedUser?.uid == uid && scannedUser.isEmailVerified

                val firestoreVerified = doc.getBoolean("isEmailVerified") ?: false
                val isEmailVerified = firestoreVerified || firebaseVerified

                if (!isEmailVerified) {
                    verificationUi = VerificationUi(
                        isValid = false,
                        title = "Not Verified",
                        name = name,
                        role = role.replaceFirstChar { it.uppercase() },
                        idLabel = "University ID",
                        idValue = universityId,
                        email = email,
                        message = "User exists, but email is not verified."
                    )
                    return@launch
                }

                val valid = universityId.isNotBlank() && universityId == qrUniversityId

                verificationUi = VerificationUi(
                    isValid = valid,
                    title = if (valid) "Verified User" else "Verification Failed",
                    name = name,
                    role = role.replaceFirstChar { it.uppercase() },
                    idLabel = "University ID",
                    idValue = universityId,
                    email = email,
                    message = if (valid) {
                        "QR belongs to a registered university user."
                    } else {
                        "University ID does not match the QR."
                    }
                )
            } catch (e: Exception) {
                Log.e("QrViewModel", "Verification failed", e)
                verificationUi = VerificationUi(
                    isValid = false,
                    title = "Verification Error",
                    name = "",
                    role = "",
                    idLabel = "",
                    idValue = "",
                    email = "",
                    message = e.message ?: "Unable to verify QR."
                )
            }
        }
    }

    fun clearVerification() {
        verificationUi = null
    }

    fun showScannerError(message: String) {
        verificationUi = VerificationUi(
            isValid = false,
            title = "Scanner Error",
            name = "",
            role = "",
            idLabel = "",
            idValue = "",
            email = "",
            message = message
        )
    }
}