package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfessorIdCardScreen(
    uid: String
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var professorId by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        if (uid.isBlank()) {
            loading = false
            error = "Professor UID is missing"
            return@LaunchedEffect
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                loading = false

                if (!doc.exists()) {
                    error = "Professor profile not found"
                    return@addOnSuccessListener
                }

                name = doc.getString("name") ?: ""
                professorId = doc.getString("professorId") ?: ""
                branch = doc.getString("branch") ?: ""
                email = doc.getString("email") ?: ""
            }
            .addOnFailureListener { ex ->
                loading = false
                error = ex.message ?: "Failed to load professor profile"
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> {
                CircularProgressIndicator()
            }

            error != null -> {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Professor ID Card",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text("Name: $name", color = Color.White, fontSize = 18.sp)
                        Text("Professor ID: $professorId", color = Color.White, fontSize = 18.sp)
                        Text("Branch: $branch", color = Color.White, fontSize = 18.sp)
                        Text("Email: $email", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}