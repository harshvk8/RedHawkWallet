package com.redhawk.wallet.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.redhawk.wallet.R
import com.redhawk.wallet.feature_auth.AuthResult
import com.redhawk.wallet.feature_auth.AuthViewModel

@Composable
fun LoginScreen(
    // keep your existing params so AppNav still compiles
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},

    // ✅ Navigate ONLY after Firebase success
    onLoginSuccess: () -> Unit = {},

    // ✅ hook to your Firebase logic
    vm: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ✅ basic validation + UI state
    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val state by vm.authState.collectAsState()
    val isLoading = state is AuthResult.Loading
    val errorText = (state as? AuthResult.Error)?.message

    // ✅ Log Firebase project id once (helps detect wrong google-services.json)
    LaunchedEffect(Unit) {
        runCatching {
            val opt = FirebaseApp.getInstance().options
            Log.d("FIREBASE", "projectId=${opt.projectId} appId=${opt.applicationId}")
        }
    }

    // ✅ React to auth state changes
    LaunchedEffect(state) {
        when (state) {
            is AuthResult.Success -> {
                Log.d("AUTH", "LOGIN SUCCESS uid=${(state as AuthResult.Success).uid}")
                vm.clearState()
                onLoginSuccess()
            }

            is AuthResult.Error -> {
                val msg = (state as AuthResult.Error).message
                Log.e("AUTH", "LOGIN FAILED: $msg")
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }

    // Start big, then shrink to normal once when screen loads
    var startAnim by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1.0f else 1.6f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    LaunchedEffect(Unit) { startAnim = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.redhawk_logo),
            contentDescription = "Red Hawk Wallet Logo",
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
            },
            label = { Text("Email") },
            singleLine = true,
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passError = null
            },
            label = { Text("Password") },
            singleLine = true,
            isError = passError != null,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (passError != null) {
            Text(
                text = passError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        // ✅ show firebase error text inline too
        if (!errorText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val e = email.trim()
                val p = password

                // ✅ block empty input
                emailError = if (e.isBlank()) "Email is required" else null
                passError = if (p.isBlank()) "Password is required" else null

                if (emailError == null && passError == null) {
                    Log.d("AUTH", "Logging in with email=$e")
                    vm.login(e, p)
                    onLoginClick(e, p) // optional legacy callback
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
            }
            Text("Login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onSignUpClick) {
            Text("Don’t have an account? Sign up")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}