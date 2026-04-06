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
import com.google.firebase.firestore.SetOptions
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
                uid = "",
                name = "Not logged in",
                studentId = ""
            )
            qrBitmap = null
            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    userProfile = QrUserProfile(
                        uid = uid,
                        name = auth.currentUser?.displayName ?: "",
                        studentId = "",
                        email = auth.currentUser?.email ?: "",
                        photoUrl = "",
                        createdAt = System.currentTimeMillis()
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
            }
            .addOnFailureListener { e ->
                Log.e("QR", "loadStudentProfile failed", e)
                userProfile = QrUserProfile(
                    uid = uid,
                    name = auth.currentUser?.displayName ?: "Profile load failed",
                    studentId = "",
                    email = auth.currentUser?.email ?: "",
                    photoUrl = "",
                    createdAt = System.currentTimeMillis()
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

                    db.collection("users")
                        .document(uid)
                        .set(mapOf("photoUrl" to url), SetOptions.merge())
                        .addOnSuccessListener {
                            userProfile = userProfile.copy(photoUrl = url)
                        }
                        .addOnFailureListener { e ->
                            Log.e("QR", "photoUrl update failed", e)
                        }
                }.addOnFailureListener { e ->
                    Log.e("QR", "download URL failed", e)
                }
            }
            .addOnFailureListener { e ->
                Log.e("QR", "uploadProfilePhoto failed", e)
            }
    }

    fun generateQrIfNeeded() {
        if (qrBitmap != null) return

        val uid = userProfile.uid.ifBlank { auth.currentUser?.uid.orEmpty() }
        val studentId = userProfile.studentId.ifBlank { "NO-STUDENT-ID" }

        if (uid.isBlank()) {
            qrBitmap = null
            return
        }

        try {
            val payload = "MSU|$uid|$studentId"
            qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
        } catch (e: Exception) {
            Log.e("QR", "QR generation failed", e)
            qrBitmap = null
        }
    }

    fun forceRefreshQr() {
        qrBitmap = null
        generateQrIfNeeded()
    }
}