package com.redhawk.wallet.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.redhawk.wallet.R
import com.redhawk.wallet.ui.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onLoginSuccess: (String, String) -> Unit = { role, uid ->
        when (role) {
            "professor" -> {
                navController.navigate("professor_id/$uid") {
                    popUpTo("login") { inclusive = true }
                }
            }
            else -> {
                navController.navigate("student_dashboard/$uid") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    var startAnim by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1.0f else 1.6f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        startAnim = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.redhawk_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(180.dp)
                .scale(logoScale)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Red Hawk Wallet",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                errorText = null
            },
            label = { Text("Email") },
            singleLine = true,
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passError = null
                errorText = null
            },
            label = { Text("Password") },
            singleLine = true,
            isError = passError != null,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Text(if (passwordVisible) "HIDE" else "SHOW")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (passError != null) {
            Text(
                text = passError!!,
                color = Color.Red
            )
        }

        if (!errorText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorText!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val e = email.trim()
                val p = password.trim()

                emailError = if (e.isBlank()) "Email is required" else null
                passError = if (p.isBlank()) "Password is required" else null

                if (emailError != null || passError != null) return@Button

                isLoading = true
                errorText = null
                onLoginClick(e, p)

                auth.signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener { result ->
                        val user = result.user
                        val uid = user?.uid

                        if (uid.isNullOrBlank()) {
                            isLoading = false
                            errorText = "UID missing"
                            return@addOnSuccessListener
                        }

                        if (!user.isEmailVerified) {
                            isLoading = false
                            navController.navigate(Routes.EMAIL_VERIFICATION_PENDING)
                            return@addOnSuccessListener
                        }

                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                isLoading = false
                                val role = doc.getString("role") ?: "student"
                                onLoginSuccess(role, uid)
                            }
                            .addOnFailureListener { ex ->
                                isLoading = false
                                errorText = ex.message ?: "Failed to load profile"
                                Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener { ex ->
                        isLoading = false
                        errorText = ex.message ?: "Login failed"
                        Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onSignUpClick) {
            Text("Don’t have an account? Sign up")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    LoginScreen(navController = rememberNavController())
}