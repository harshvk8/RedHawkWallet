package com.redhawk.wallet.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.redhawk.wallet.R

private fun isValidName(name: String): Boolean {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return false
    return trimmed.all { it.isLetter() || it == ' ' }
}

private fun isValidUsername(username: String): Boolean {
    val trimmed = username.trim()
    if (trimmed.length < 3) return false
    return trimmed.all { it.isLetterOrDigit() || it == '_' || it == '.' }
}

private fun isValidMontclairEmail(email: String): Boolean {
    val trimmed = email.trim()
    return trimmed.contains("@") && trimmed.endsWith("@montclair.edu", ignoreCase = true)
}

private fun isValidStudentId(studentId: String): Boolean {
    val trimmed = studentId.trim()
    if (trimmed.length != 9) return false
    if (trimmed[0] != 'M') return false
    return trimmed.substring(1).all { it.isDigit() }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (name: String, username: String, studentId: String, email: String, password: String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var studentIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordMatchError by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isRegistering by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf<String?>(null) }

    val hasCapitalLetter = password.any { it.isUpperCase() }
    val hasNumber = password.any { it.isDigit() }
    val passwordsMatch = confirmPassword.isNotEmpty() && password == confirmPassword

    val isPasswordValid =
        password.isNotBlank() && hasCapitalLetter && hasNumber && passwordsMatch

    val isFormValid =
        name.isNotBlank() &&
                username.isNotBlank() &&
                studentId.isNotBlank() &&
                email.isNotBlank() &&
                isPasswordValid &&
                !nameError &&
                !usernameError &&
                !studentIdError &&
                !emailError &&
                !passwordMatchError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.redhawk_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(110.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Full Name
        OutlinedTextField(
            value = name,
            onValueChange = { input ->
                val filtered = input.filter { it.isLetter() || it == ' ' }
                name = filtered
                nameError = filtered.isNotEmpty() && !isValidName(filtered)
            },
            label = { Text("Full Name") },
            singleLine = true,
            isError = nameError,
            supportingText = { if (nameError) Text("Name must contain letters only") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { input ->
                val filtered = input.filter { it.isLetterOrDigit() || it == '_' || it == '.' }
                username = filtered
                usernameError = filtered.isNotEmpty() && !isValidUsername(filtered)
            },
            label = { Text("Username") },
            singleLine = true,
            isError = usernameError,
            supportingText = {
                if (usernameError) {
                    Text("At least 3 characters. Letters, numbers, _ and . only")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Student ID
        OutlinedTextField(
            value = studentId,
            onValueChange = { input ->
                val upper = input.uppercase()
                val filtered = upper.filterIndexed { index, c ->
                    if (index == 0) c == 'M' || c.isDigit() else c.isDigit()
                }
                studentId = filtered.take(9)
                studentIdError = studentId.isNotEmpty() && !isValidStudentId(studentId)
            },
            label = { Text("Student ID (M########)") },
            singleLine = true,
            isError = studentIdError,
            supportingText = {
                if (studentIdError) Text("Must start with M + 8 digits (ex: M12345678)")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { input ->
                email = input.trim()
                emailError = email.isNotEmpty() && !isValidMontclairEmail(email)
            },
            label = { Text("Email (@montclair.edu)") },
            singleLine = true,
            isError = emailError,
            supportingText = { if (emailError) Text("Email must end with @montclair.edu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                passwordMatchError = confirmPassword.isNotEmpty() && password != confirmPassword
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "HIDE" else "SHOW")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (password.isNotEmpty() && (!hasCapitalLetter || !hasNumber)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Password must include 1 capital letter and 1 number",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { newValue ->
                confirmPassword = newValue
                passwordMatchError = confirmPassword.isNotEmpty() && password != confirmPassword
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            isError = passwordMatchError,
            supportingText = { if (passwordMatchError) Text("Passwords do not match") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Text(if (confirmPasswordVisible) "HIDE" else "SHOW")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!registerError.isNullOrBlank()) {
            Text(
                text = registerError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                registerError = null

                if (!isFormValid) {
                    Toast.makeText(context, "Please fix errors before registering", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isRegistering = true

                val e = email.trim()
                val p = password
                val u = username.trim().lowercase()

                Log.d("AUTH", "Registering email=$e username=$u")

                // Check if username already exists
                db.collection("users")
                    .whereEqualTo("username", u)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            isRegistering = false
                            registerError = "Username already taken"
                            return@addOnSuccessListener
                        }

                        auth.createUserWithEmailAndPassword(e, p)
                            .addOnSuccessListener {
                                val uid = auth.currentUser?.uid
                                if (uid == null) {
                                    isRegistering = false
                                    registerError = "Registered but UID missing"
                                    return@addOnSuccessListener
                                }

                                val userMap = hashMapOf(
                                    "name" to name.trim(),
                                    "username" to u,
                                    "studentId" to studentId.trim(),
                                    "email" to e
                                )

                                db.collection("users").document(uid).set(userMap)
                                    .addOnSuccessListener {
                                        isRegistering = false
                                        Log.d("AUTH", "REGISTER+PROFILE OK uid=$uid")
                                        Toast.makeText(context, "Registered!", Toast.LENGTH_SHORT).show()

                                        onRegisterClick(name.trim(), u, studentId.trim(), e, p)
                                        onBackToLoginClick()
                                    }
                                    .addOnFailureListener { ex ->
                                        isRegistering = false
                                        Log.e("AUTH", "PROFILE SAVE FAILED", ex)
                                        registerError = "Profile save failed: ${ex.message}"
                                    }
                            }
                            .addOnFailureListener { ex ->
                                isRegistering = false
                                Log.e("AUTH", "REGISTER FAILED", ex)
                                registerError = ex.message ?: "Register failed"
                            }
                    }
                    .addOnFailureListener { ex ->
                        isRegistering = false
                        registerError = "Could not verify username: ${ex.message}"
                    }
            },
            enabled = !isRegistering,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isRegistering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(if (isRegistering) "Creating..." else "Register")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = { if (!isRegistering) onBackToLoginClick() }) {
            Text("Already have an account? Login")
        }
    }
}




LoginScreen::
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
import com.google.firebase.firestore.FirebaseFirestore
import com.redhawk.wallet.R
import com.redhawk.wallet.feature_auth.AuthResult
import com.redhawk.wallet.feature_auth.AuthViewModel

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onSignUpClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    vm: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailOrUsernameError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var localLoading by remember { mutableStateOf(false) }

    val state by vm.authState.collectAsState()
    val isLoading = state is AuthResult.Loading || localLoading
    val errorText = (state as? AuthResult.Error)?.message

    LaunchedEffect(Unit) {
        runCatching {
            val opt = FirebaseApp.getInstance().options
            Log.d("FIREBASE", "projectId=${opt.projectId} appId=${opt.applicationId}")
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is AuthResult.Success -> {
                Log.d("AUTH", "LOGIN SUCCESS uid=${(state as AuthResult.Success).uid}")
                localLoading = false
                vm.clearState()
                onLoginSuccess()
            }

            is AuthResult.Error -> {
                val msg = (state as AuthResult.Error).message
                localLoading = false
                Log.e("AUTH", "LOGIN FAILED: $msg")
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }

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
            value = emailOrUsername,
            onValueChange = {
                emailOrUsername = it
                emailOrUsernameError = null
            },
            label = { Text("Email or Username") },
            singleLine = true,
            isError = emailOrUsernameError != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (emailOrUsernameError != null) {
            Text(
                text = emailOrUsernameError!!,
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
                val input = emailOrUsername.trim()
                val p = password.trim()

                emailOrUsernameError =
                    if (input.isBlank()) "Email or username is required" else null
                passError =
                    if (p.isBlank()) "Password is required" else null

                if (emailOrUsernameError != null || passError != null) return@Button

                localLoading = true

                if (input.contains("@")) {
                    Log.d("AUTH", "Logging in with email=$input")
                    vm.login(input, p)
                    onLoginClick(input, p)
                } else {
                    val usernameInput = input.lowercase()

                    db.collection("users")
                        .whereEqualTo("username", usernameInput)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { result ->
                            if (result.isEmpty) {
                                localLoading = false
                                emailOrUsernameError = "Username not found"
                                return@addOnSuccessListener
                            }

                            val doc = result.documents.first()
                            val email = doc.getString("email")

                            if (email.isNullOrBlank()) {
                                localLoading = false
                                emailOrUsernameError = "No email found for this username"
                                return@addOnSuccessListener
                            }

                            Log.d("AUTH", "Username resolved to email=$email")
                            vm.login(email, p)
                            onLoginClick(email, p)
                        }
                        .addOnFailureListener { ex ->
                            localLoading = false
                            emailOrUsernameError = ex.message ?: "Username lookup failed"
                        }
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

