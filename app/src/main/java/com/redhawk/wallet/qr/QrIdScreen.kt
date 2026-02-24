package com.redhawk.wallet.qr

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrIdScreen(
    modifier: Modifier = Modifier,
    vm: QrViewModel = viewModel()
) {
    // UI state
    var showQr by remember { mutableStateOf(false) }

    // data from ViewModel (mutableStateOf)
    val student = vm.student
    val qrBmp = vm.qrBitmap

    // Colors (white + red)
    val Red = Color(0xFFC8102E)
    val RedDark = Color(0xFF9E0B22)
    val Border = Color(0xFFE6E6E6)
    val Text = Color(0xFF1F1F1F)
    val Muted = Color(0xFF666666)

    // Accounts list (demo)
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
                title = {
                    Text(
                        "Account Services",
                        fontWeight = FontWeight.Bold,
                        color = Text
                    )
                }
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                student.name,
                                fontWeight = FontWeight.Bold,
                                color = Text,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                student.studentId,
                                color = Muted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "UID: ${student.uid}",
                                color = Muted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        OutlinedButton(
                            onClick = { /* later: upload photo */ },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Red),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Red)
                        ) {
                            Text("Photo", fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        "• Latest photo required at the start of every semester.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedButton(
                        onClick = { /* later: upload new photo */ },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Border),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Text),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Upload New Photo")
                    }
                }
            }

            // --- Show QR button ---
            Button(
                onClick = {
                    showQr = true
                    vm.generateQrIfNeeded()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Account QR Code", fontWeight = FontWeight.Bold)
            }

            // --- QR section (only when button clicked) ---
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
                            onClick = {
                                // regenerate if you want (optional)
                                vm.setStudent(student.name, student.studentId, student.uid)
                                vm.generateQrIfNeeded()
                            },
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

            // --- Dropdown (Select Account) ---
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            // --- Close button ---
            Button(
                onClick = { showQr = false },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    }
}
