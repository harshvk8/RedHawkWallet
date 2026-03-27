package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.redhawk.wallet.feature_auth.AuthViewModel

@Composable
fun ProfessorIdCardScreen(
    vm: AuthViewModel = viewModel()
) {
    val uid = vm.authState.collectAsState().value.let { state ->
        (state as? com.redhawk.wallet.feature_auth.AuthResult.Success)?.uid
    }

    var profile by remember { mutableStateOf<com.redhawk.wallet.data.models.UserProfile?>(null) }

    LaunchedEffect(uid) {
        if (uid != null) {
            // fetch professor profile
            profile = vm.getUserProfile(uid)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        profile?.let { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
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
                    Text("Name: ${user.name}", color = Color.White, fontSize = 18.sp)
                    Text("Professor ID: ${user.professorId}", color = Color.White, fontSize = 18.sp)
                    Text("Branch: ${user.branch}", color = Color.White, fontSize = 18.sp)
                    Text("Email: ${user.email}", color = Color.White, fontSize = 18.sp)
                }
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}