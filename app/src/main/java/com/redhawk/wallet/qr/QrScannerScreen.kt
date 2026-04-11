package com.redhawk.wallet.qr

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    navController: NavController,
    vm: QrViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var hasScanned by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    val verification = vm.verificationUi
    val red = Color(0xFFC8102E)
    val redDark = Color(0xFF9E0B22)
    val successGreen = Color(0xFF2E7D32)

    LaunchedEffect(Unit) {
        vm.clearVerification()
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(verification) {
        if (verification != null) {
            isVerifying = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    bindQrCamera(
                        previewView = previewView,
                        lifecycleOwner = lifecycleOwner,
                        onQrScanned = { rawValue ->
                            if (!hasScanned) {
                                hasScanned = true
                                isVerifying = true
                                vm.verifyScannedQr(rawValue)
                            }
                        }
                    )
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.20f))
            )

            ScannerOverlay()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Scan ID QR",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Text(
                    text = "Place the QR code inside the square",
                    color = Color.White,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            if (isVerifying) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Text(
                        text = "Verifying...",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                        color = redDark,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            verification?.let { result ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + scaleIn()
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.isValid) successGreen else red
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (result.isValid) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                } else {
                                    Text(
                                        text = "✕",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }

                                Text(
                                    text = result.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }

                            if (result.name.isNotBlank()) {
                                Text("Name: ${result.name}", color = Color.White)
                            }

                            if (result.role.isNotBlank()) {
                                Text("Role: ${result.role}", color = Color.White)
                            }

                            if (result.idLabel.isNotBlank()) {
                                Text("${result.idLabel}: ${result.idValue}", color = Color.White)
                            }

                            if (result.message.isNotBlank()) {
                                Text(result.message, color = Color.White)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        hasScanned = false
                                        isVerifying = false
                                        vm.clearVerification()
                                    },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, Color.White),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Scan Again")
                                }

                                Button(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = if (result.isValid) successGreen else redDark
                                    )
                                ) {
                                    Text("Back")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Camera permission is required to scan QR codes.",
                    color = Color.White
                )

                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = red)
                ) {
                    Text("Allow Camera")
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 12.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Back")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.clearVerification()
        }
    }
}

@Composable
private fun ScannerOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
        )

        Canvas(
            modifier = Modifier
                .size(280.dp)
                .offset(y = 0.dp)
        ) {
            val corner = 36.dp.toPx()
            val stroke = 8.dp.toPx()
            val w = size.width
            val h = size.height
            val c = Color(0xFFC8102E)

            drawLine(c, Offset(0f, corner), Offset(0f, 0f), strokeWidth = stroke)
            drawLine(c, Offset(0f, 0f), Offset(corner, 0f), strokeWidth = stroke)

            drawLine(c, Offset(w - corner, 0f), Offset(w, 0f), strokeWidth = stroke)
            drawLine(c, Offset(w, 0f), Offset(w, corner), strokeWidth = stroke)

            drawLine(c, Offset(0f, h - corner), Offset(0f, h), strokeWidth = stroke)
            drawLine(c, Offset(0f, h), Offset(corner, h), strokeWidth = stroke)

            drawLine(c, Offset(w - corner, h), Offset(w, h), strokeWidth = stroke)
            drawLine(c, Offset(w, h - corner), Offset(w, h), strokeWidth = stroke)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun bindQrCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    onQrScanned: (String) -> Unit
) {
    val context = previewView.context
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    val scanner = BarcodeScanning.getClient()

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also { previewUseCase ->
            previewUseCase.setSurfaceProvider(previewView.surfaceProvider)
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val raw = barcodes.firstOrNull()?.rawValue
                        if (!raw.isNullOrBlank()) {
                            onQrScanned(raw)
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
        } catch (_: Exception) {
        }
    }, ContextCompat.getMainExecutor(context))
}