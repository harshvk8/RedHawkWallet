package com.redhawk.wallet.qr

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val scanner = BarcodeScanning.getClient()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val firestore = FirebaseFirestore.getInstance()

    var scannedUserName by remember { mutableStateOf("") }
    var scannedUserId by remember { mutableStateOf("") }
    var scannedUserExists by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Scan QR Code",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val analyzer = ImageAnalysis.Builder().build().also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )

                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { uid ->
                                                firestore.collection("users")
                                                    .document(uid)
                                                    .get()
                                                    .addOnSuccessListener { doc ->
                                                        if (doc.exists()) {
                                                            scannedUserExists = true
                                                            scannedUserName =
                                                                doc.getString("name") ?: "Unknown User"
                                                            scannedUserId = uid
                                                        } else {
                                                            scannedUserExists = false
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as androidx.lifecycle.LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analyzer
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        if (scannedUserExists) {
            Text(
                text = "Send money to: $scannedUserName",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Button(
                onClick = {
                    sendMoney(scannedUserId, amount)
                    Toast.makeText(context, "Payment Sent!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Send Money")
            }
        }
    }
}

fun sendMoney(receiverUid: String, amount: String) {
    val senderUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = FirebaseFirestore.getInstance()

    val amt = amount.toDoubleOrNull() ?: return

    val senderRef = firestore.collection("users").document(senderUid)
    val receiverRef = firestore.collection("users").document(receiverUid)

    firestore.runTransaction { transaction ->
        val senderSnap = transaction.get(senderRef)
        val receiverSnap = transaction.get(receiverRef)

        val senderBalance = senderSnap.getDouble("balance") ?: 0.0
        val receiverBalance = receiverSnap.getDouble("balance") ?: 0.0

        if (senderBalance < amt) {
            throw Exception("Not enough balance")
        }

        transaction.update(senderRef, "balance", senderBalance - amt)
        transaction.update(receiverRef, "balance", receiverBalance + amt)
    }
}