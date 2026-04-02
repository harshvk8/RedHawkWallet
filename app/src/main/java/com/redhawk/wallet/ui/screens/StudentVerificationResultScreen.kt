package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentVerificationResultScreen(
    navController: NavController,
    studentUid: String
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var studentName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var studentEmail by remember { mutableStateOf("") }
    var studentRole by remember { mutableStateOf("") }

    LaunchedEffect(studentUid) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(studentUid)
            .get()
            .addOnSuccessListener { doc ->
                loading = false

                if (!doc.exists()) {
                    error = "Student not found"
                    return@addOnSuccessListener
                }

                studentName = doc.getString("name") ?: ""
                studentId = doc.getString("studentId") ?: ""
                studentEmail = doc.getString("email") ?: ""
                studentRole = doc.getString("role") ?: ""

                if (studentRole != "student") {
                    error = "This QR does not belong to a student"
                }
            }
            .addOnFailureListener { ex ->
                loading = false
                error = ex.message ?: "Failed to fetch student"
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification Result") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Student Verified",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Name: $studentName")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Student ID: $studentId")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Email: $studentEmail")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Role: $studentRole")

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }
        }
    }
}