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
        val fallbackStudentId = userProfile.studentId

        if (uid.isBlank()) {
            errorMessage = "Cannot generate QR without a valid user."
            return
        }

        viewModelScope.launch {
            try {
                val doc = db.collection("users")
                    .document(uid)
                    .get()
                    .await()

                val role = (doc.getString("role") ?: userProfile.role).lowercase()
                val studentId = doc.getString("studentId") ?: fallbackStudentId
                val professorId = doc.getString("professorId") ?: ""

                val idForQr = if (role == "professor") {
                    professorId.ifBlank { studentId }
                } else {
                    studentId
                }

                if (idForQr.isBlank()) {
                    errorMessage = "Cannot generate QR without an ID."
                    return@launch
                }

                val payload = "MSU|$uid|$idForQr"
                qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
                errorMessage = null
            } catch (e: Exception) {
                Log.e("QrViewModel", "Failed to generate QR", e)
                qrBitmap = null
                errorMessage = e.message ?: "QR generation failed."
            }
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
        val qrId = parts[2]

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
                val studentId = doc.getString("studentId") ?: ""
                val professorId = doc.getString("professorId") ?: ""
                val isEmailVerified = doc.getBoolean("isEmailVerified") ?: false

                if (!isEmailVerified) {
                    verificationUi = VerificationUi(
                        isValid = false,
                        title = "Not Verified",
                        name = name,
                        role = role,
                        idLabel = if (role == "professor") "Professor ID" else "Student ID",
                        idValue = if (role == "professor") professorId.ifBlank { studentId } else studentId,
                        email = email,
                        message = "User exists, but email is not verified."
                    )
                    return@launch
                }

                if (role == "professor") {
                    val matchedId = when {
                        professorId.isNotBlank() && qrId == professorId -> professorId
                        studentId.isNotBlank() && qrId == studentId -> studentId
                        else -> ""
                    }

                    val valid = matchedId.isNotBlank()

                    verificationUi = VerificationUi(
                        isValid = valid,
                        title = if (valid) "Verified Professor" else "Professor Verification Failed",
                        name = name,
                        role = "Professor",
                        idLabel = "Professor ID",
                        idValue = professorId.ifBlank { studentId },
                        email = email,
                        message = if (valid) {
                            "QR belongs to a registered professor."
                        } else {
                            "Scanned QR does not match professor records."
                        }
                    )
                } else {
                    val valid = studentId.isNotBlank() && studentId == qrId

                    verificationUi = VerificationUi(
                        isValid = valid,
                        title = if (valid) "Verified Student" else "Student Verification Failed",
                        name = name,
                        role = "Student",
                        idLabel = "Student ID",
                        idValue = studentId,
                        email = email,
                        message = if (valid) {
                            "QR belongs to a registered student."
                        } else {
                            "Student ID does not match the QR."
                        }
                    )
                }
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