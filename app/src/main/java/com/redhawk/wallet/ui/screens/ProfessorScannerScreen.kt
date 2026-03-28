package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorScannerScreen(
    navController: NavController
) {
    var qrValue by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Professor Scanner") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Paste or type the student UID from the QR code for now.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = qrValue,
                onValueChange = {
                    qrValue = it
                    errorText = null
                },
                label = { Text("Student UID from QR") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            errorText?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val cleaned = qrValue.trim()
                    if (cleaned.isBlank()) {
                        errorText = "Enter a valid student UID"
                    } else {
                        navController.navigate("${Routes.STUDENT_VERIFY_RESULT}/$cleaned")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verify Student")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}