package com.redhawk.wallet.qr

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val firebaseUser = auth.currentUser

    var showQr by remember { mutableStateOf(false) }

    val student = vm.userProfile
    val qrBmp = vm.qrBitmap

    // ✅ Image picker MUST be inside composable
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

    // ✅ FALLBACKS so text never shows blank
    val displayName = student.name.ifBlank { firebaseUser?.displayName ?: "Unknown User" }
    val displayEmail = student.email.ifBlank { firebaseUser?.email ?: "" }
    val displayUid = student.uid.ifBlank { firebaseUser?.uid ?: "" }

    val Red = Color(0xFFC8102E)
    val RedDark = Color(0xFF9E0B22)
    val Border = Color(0xFFE6E6E6)
    val Text = Color(0xFF1F1F1F)
    val Muted = Color(0xFF666666)

    val accounts = listOf(
        "Red Hawk Dollars Debit",
        "Red Hawk Dollars Flex",
        "Red Hawk Dollars Bonus",
        "Meal Swipes (Entries)"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedAccount by remember { mutableStateOf(accounts.first()) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account Services", fontWeight = FontWeight.Bold, color = Text) }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // --- ID Card ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "MONTCLAIR\nSTATE UNIVERSITY",
                        fontWeight = FontWeight.Black,
                        color = Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Montclair Red Hawk Campus Visual",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // ✅ Photo on left
                        AsyncImage(
                            model = student.photoUrl.takeIf { it.isNotBlank() },  // ✅ only load if non-empty
                            contentDescription = "Profile Photo",
                            modifier = Modifier.size(72.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        // ✅ Name + Email + UID (with fallbacks)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name.ifBlank { "Unknown User" },
                                fontWeight = FontWeight.Bold,
                                color = Text,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = student.studentId.ifBlank { "—" },   // ✅ show studentId (NOT email)
                                color = Muted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "UID: ${student.uid.ifBlank { "—" }}",
                                color = Muted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // ✅ Upload button
                        OutlinedButton(
                            onClick = { pickImage.launch("image/*") },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Red),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Red)
                        ) {
                            Text("Upload", fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        "• Latest photo required at the start of every semester.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // --- Show QR button ---
            Button(
                onClick = {
                    if (!showQr) {
                        showQr = true
                        vm.generateQrIfNeeded()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (showQr) "QR Code Shown" else "Show Account QR Code",
                    fontWeight = FontWeight.Bold
                )
            }

            // --- QR section ---
            if (showQr) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Border),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Scan this QR for verification", fontWeight = FontWeight.Bold, color = Text)

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
                            border = BorderStroke(1.dp, RedDark),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedDark)
                        ) {
                            Text("Refresh QR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(
                "Select Account",
                color = Text,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )

            // --- Dropdown ---
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedAccount,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Red,
                        unfocusedBorderColor = Border,
                        focusedTextColor = Text,
                        unfocusedTextColor = Text
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    accounts.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedAccount = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Bottom: Back or Logout ---
            if (showQr) {
                Button(
                    onClick = { showQr = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedDark),
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
                    colors = ButtonDefaults.buttonColors(containerColor = RedDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        navController.navigate(Routes.QR_SCANNER)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Scan QR Code")
                }
            }
        }
    }
}