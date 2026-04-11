package com.redhawk.wallet.qr

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrIdScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    vm: QrViewModel = viewModel()
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val firebaseUser = auth.currentUser
    val context = LocalContext.current


    var showQr by remember { mutableStateOf(false) }

    val student = vm.userProfile
    val qrBmp = vm.qrBitmap
    val verificationUi = vm.verificationUi

    val displayName = student.name.ifBlank { firebaseUser?.displayName ?: "Unknown User" }
    val displayStudentId = student.studentId.ifBlank { "—" }
    val displayEmail = student.email.ifBlank { firebaseUser?.email ?: "—" }
    val displayUid = student.uid.ifBlank { firebaseUser?.uid ?: "—" }

    val red = Color(0xFFC8102E)
    val redDark = Color(0xFF9E0B22)
    val border = Color(0xFFE6E6E6)
    val textColor = Color(0xFF1F1F1F)
    val muted = Color(0xFF666666)

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            vm.uploadProfilePhoto(uri)
            Toast.makeText(context, "Uploading photo...", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        vm.loadStudentProfile()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Account Services",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "MONTCLAIR\nSTATE UNIVERSITY",
                        fontWeight = FontWeight.Black,
                        color = red,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Montclair Red Hawk Campus Visual",
                        color = muted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!student.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = student.photoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayName.firstOrNull()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = textColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = displayStudentId,
                                color = muted,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = displayEmail,
                                color = muted,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = "UID: $displayUid",
                                color = muted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { pickImage.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, red),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload Photo", fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "• Latest photo required at the start of every semester.",
                        color = muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            vm.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    if (!showQr) {
                        showQr = true
                        vm.generateQrIfNeeded()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (showQr) "QR Code Shown" else "Show Account QR Code",
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.QR_SCANNER)
                },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, redDark),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = redDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan & Verify", fontWeight = FontWeight.Bold)
            }

            verificationUi?.let { result ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (result.isValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = result.title,
                            fontWeight = FontWeight.Bold,
                            color = if (result.isValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (result.name.isNotBlank()) {
                            Text(
                                text = "Name: ${result.name}",
                                color = textColor
                            )
                        }

                        if (result.role.isNotBlank()) {
                            Text(
                                text = "Role: ${result.role}",
                                color = textColor
                            )
                        }

                        if (result.idLabel.isNotBlank()) {
                            Text(
                                text = "${result.idLabel}: ${result.idValue}",
                                color = textColor
                            )
                        }

                        if (result.email.isNotBlank()) {
                            Text(
                                text = "Email: ${result.email}",
                                color = textColor
                            )
                        }

                        Text(
                            text = result.message,
                            color = muted
                        )

                        OutlinedButton(
                            onClick = { vm.clearVerification() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Clear Verification")
                        }
                    }
                }
            }

            if (showQr) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, border),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Scan this QR for verification",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Box(
                            modifier = Modifier.size(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (qrBmp == null) {
                                CircularProgressIndicator()
                            } else {
                                Image(
                                    bitmap = qrBmp.asImageBitmap(),
                                    contentDescription = "Student QR Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { vm.forceRefreshQr() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, redDark),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = redDark)
                        ) {
                            Text("Refresh QR", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = { showQr = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = redDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = redDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}