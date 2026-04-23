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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.redhawk.wallet.ui.theme.ThemeViewModel

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
    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    var showQr by remember { mutableStateOf(false) }

    val user = vm.userProfile
    val qrBmp = vm.qrBitmap
    val verificationUi = vm.verificationUi

    val displayName = user.name.ifBlank { firebaseUser?.displayName ?: "Unknown User" }
    val displayId = user.universityId.ifBlank { "—" }
    val displayEmail = user.email.ifBlank { firebaseUser?.email ?: "—" }
    val displayUid = user.uid.ifBlank { firebaseUser?.uid ?: "—" }

    val red = Color(0xFFC8102E)
    val redDark = Color(0xFF483F48)

    val border = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            vm.uploadProfilePhoto(uri)
            Toast.makeText(context, "Uploading photo...", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        vm.loadUserProfile()
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Account Services",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
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
                colors = CardDefaults.cardColors(containerColor = surfaceVariantColor),
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
                        if (!user.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = user.photoUrl,
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
                                text = "University ID: $displayId",
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

            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dark Mode",
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Change the color of the whole application",
                            color = muted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { themeViewModel.setDarkMode(it) }
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

            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.EVENTS_OFFERS)
                },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, redDark),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = redDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Events & Offers", fontWeight = FontWeight.Bold)
            }

            verificationUi?.let { result ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (result.isValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    ),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
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
                            Text(text = "Name: ${result.name}", color = textColor)
                        }

                        if (result.role.isNotBlank()) {
                            Text(text = "Role: ${result.role}", color = textColor)
                        }

                        if (result.idLabel.isNotBlank()) {
                            Text(text = "${result.idLabel}: ${result.idValue}", color = textColor)
                        }

                        if (result.email.isNotBlank()) {
                            Text(text = "Email: ${result.email}", color = textColor)
                        }

                        Text(text = result.message, color = muted)

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
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
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
                                    contentDescription = "User QR Code",
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