package com.redhawk.wallet.qr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.redhawk.wallet.data.models.UserProfile
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
data class StudentUi(
    val name: String,
    val studentId: String,
    val uid: String
)


class QrViewModel : ViewModel() {
    private val storage = FirebaseStorage.getInstance()

    fun uploadProfilePhoto(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        val ref = storage.reference.child("profile_photos/$uid.jpg")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()

                    // ✅ Save HTTPS URL in Firestore
                    db.collection("users").document(uid)
                        .update("photoUrl", url)
                        .addOnSuccessListener {
                            // ✅ Update local state so UI refreshes instantly
                            userProfile = userProfile.copy(photoUrl = url)
                            Log.d("QR", "Photo saved + state updated: $url")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QR", "uploadProfilePhoto FAILED", e)
            }
    }
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var student by mutableStateOf(
        StudentUi(
            name = "Loading...",
            studentId = "Loading...",
            uid = auth.currentUser?.uid ?: "NO-USER"
        )
    )
        private set

    var qrBitmap by mutableStateOf<Bitmap?>(null)
        private set

    /** Call this when screen opens */
    fun loadStudentProfile() {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            userProfile = UserProfile(uid = "NO-USER", name = "Not logged in", studentId = "—")
            qrBitmap = null
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                Log.d("QR", "doc exists = ${doc.exists()}")
                Log.d("QR", "doc data = ${doc.data}")

                if (!doc.exists()) {
                    userProfile = UserProfile(uid = uid, name = "NO FIRESTORE DOC", studentId = "Create profile")
                    qrBitmap = null
                    return@addOnSuccessListener
                }

                val name = doc.getString("name").orEmpty()
                val studentId = doc.getString("studentId").orEmpty()
                val email = doc.getString("email").orEmpty()
                val photoUrl = doc.getString("photoUrl").orEmpty()

                userProfile = UserProfile(
                    uid = uid,
                    name = name,
                    studentId = studentId,
                    email = email,
                    photoUrl = photoUrl,
                    createdAt = (doc.getLong("createdAt") ?: System.currentTimeMillis())
                )

                qrBitmap = null
                Log.d("QR", "Loaded profile OK uid=$uid name=$name studentId=$studentId photoUrl=$photoUrl")
            }
            .addOnFailureListener { e ->
                Log.e("QR", "loadStudentProfile FAILED", e)
                userProfile = UserProfile(uid = uid, name = "Profile load failed", studentId = "—")
                qrBitmap = null
            }
    }

    fun generateQrIfNeeded() {
        if (qrBitmap != null) return
        val payload = "MSU|${student.uid}|${student.studentId}"
        qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
    }
    var userProfile by mutableStateOf(UserProfile())
        private set
    /** Used by Refresh QR button */
    fun forceRefreshQr() {
        qrBitmap = null
        generateQrIfNeeded()
    }
}