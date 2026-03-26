package com.redhawk.wallet.qr

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

data class QrUserProfile(
    val uid: String = "",
    val name: String = "",
    val studentId: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val createdAt: Long = 0L
)

class QrViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    var userProfile by mutableStateOf(QrUserProfile())
        private set

    var qrBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun loadStudentProfile() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            userProfile = QrUserProfile(
                uid = "NO-USER",
                name = "Not logged in",
                studentId = "—"
            )
            qrBitmap = null
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                Log.d("QR", "doc exists = ${doc.exists()}")
                Log.d("QR", "doc data = ${doc.data}")

                if (!doc.exists()) {
                    userProfile = QrUserProfile(
                        uid = uid,
                        name = "NO FIRESTORE DOC",
                        studentId = "Create profile"
                    )
                    qrBitmap = null
                    return@addOnSuccessListener
                }

                userProfile = QrUserProfile(
                    uid = uid,
                    name = doc.getString("name").orEmpty(),
                    studentId = doc.getString("studentId").orEmpty(),
                    email = doc.getString("email").orEmpty(),
                    photoUrl = doc.getString("photoUrl").orEmpty(),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )

                qrBitmap = null
                Log.d("QR", "Loaded profile OK: $userProfile")
            }
            .addOnFailureListener { e ->
                Log.e("QR", "loadStudentProfile FAILED", e)
                userProfile = QrUserProfile(
                    uid = uid,
                    name = "Profile load failed",
                    studentId = "—"
                )
                qrBitmap = null
            }
    }

    fun uploadProfilePhoto(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("profile_photos/$uid.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()

                    db.collection("users").document(uid)
                        .update("photoUrl", url)
                        .addOnSuccessListener {
                            userProfile = userProfile.copy(photoUrl = url)
                            Log.d("QR", "Photo saved + state updated: $url")
                        }
                        .addOnFailureListener { e ->
                            Log.e("QR", "Failed to update Firestore photoUrl", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QR", "uploadProfilePhoto FAILED", e)
            }
    }

    fun generateQrIfNeeded() {
        if (qrBitmap != null) return

        val payload = "MSU|${userProfile.uid}|${userProfile.studentId}"
        qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
    }

    fun forceRefreshQr() {
        qrBitmap = null
        generateQrIfNeeded()
    }
}