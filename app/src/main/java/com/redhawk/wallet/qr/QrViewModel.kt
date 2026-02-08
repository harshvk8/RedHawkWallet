package com.redhawk.wallet.qr

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class StudentUi(
    val name: String,
    val studentId: String,
    val uid: String
)

class QrViewModel : ViewModel() {

    var student by mutableStateOf(
        StudentUi(
            name = "Demo Student",
            studentId = "12345678",
            uid = "UID-DEMO-12345678"
        )
    )
        private set

    var qrBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun generateQrIfNeeded() {
        if (qrBitmap != null) return

        val payload = "MSU|${student.uid}|${student.studentId}"
        qrBitmap = QrCodeGenerator.generateQrBitmap(payload)
    }

    // Later, call this after login to update the QR for real user
    fun setStudent(name: String, studentId: String, uid: String) {
        student = StudentUi(name, studentId, uid)
        qrBitmap = null
    }
}