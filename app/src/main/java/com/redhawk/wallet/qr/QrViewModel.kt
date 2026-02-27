package com.redhawk.wallet.qr

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class StudentUi(
    val name: String,
    val studentId: String,
    val uid: String
)

class QrViewModel : ViewModel() {

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
            student = StudentUi("Not logged in", "—", "NO-USER")
            qrBitmap = null
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->

                // ✅ DEBUG LOGS
                Log.d("QR", "doc exists = ${doc.exists()}")
                Log.d("QR", "doc data = ${doc.data}")

                if (!doc.exists()) {
                    student = StudentUi("NO FIRESTORE DOC", "Create profile", uid)
                    qrBitmap = null
                    return@addOnSuccessListener
                }

                val name = doc.getString("name") ?: "Missing 'name' field"
                val studentId = doc.getString("studentId") ?: "Missing 'studentId' field"

                student = StudentUi(
                    name = name,
                    studentId = studentId,
                    uid = uid
                )
                qrBitmap = null
                Log.d("QR", "Loaded profile OK uid=$uid name=$name studentId=$studentId")
            }
            .addOnFailureListener { e ->
                Log.e("QR", "loadStudentProfile FAILED", e)
                student = StudentUi("Profile load failed", "—", uid)
                qrBitmap = null
            }
    }

    fun generateQrIfNeeded() {
        if (qrBitmap != null) return
        val payload = "MSU|${student.uid}|${student.studentId}"
        qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
    }

    /** Used by Refresh QR button */
    fun forceRefreshQr() {
        qrBitmap = null
        generateQrIfNeeded()
    }
}