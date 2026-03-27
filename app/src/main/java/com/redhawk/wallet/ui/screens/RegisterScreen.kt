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
    onRegisterClick: (name: String, studentId: String, email: String, password: String) -> Unit,
    onBackToLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var studentIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordMatchError by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isRegistering by remember { mutableStateOf(false) }
    var registerError by remember { mutableStateOf<String?>(null) }

    // Professor fields
    var isProfessor by remember { mutableStateOf(false) }
    var professorId by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    val hasCapitalLetter = password.any { it.isUpperCase() }
    val hasNumber = password.any { it.isDigit() }
    val passwordsMatch = confirmPassword.isNotEmpty() && password == confirmPassword

    val isPasswordValid =
        password.isNotBlank() && hasCapitalLetter && hasNumber && passwordsMatch

    val isFormValid =
        name.isNotBlank() &&
                studentId.isNotBlank() &&
                email.isNotBlank() &&
                isPasswordValid &&
                (!isProfessor || (professorId.isNotBlank() && branch.isNotBlank())) && // Added Professor check
                !nameError &&
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

        // --- FULL NAME FIELD ---
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
            supportingText = {
                if (nameError) {
                    Text("Name must contain letters only")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- STUDENT ID FIELD ---
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
                if (studentIdError) {
                    Text("Must start with M + 8 digits (ex: M12345678)")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- PROFESSOR TOGGLE + FIELDS ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = isProfessor,
                onCheckedChange = { isProfessor = it }
            )
            Text("Register as Professor")
        }

        if (isProfessor) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = professorId,
                onValueChange = { professorId = it },
                label = { Text("Professor ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = branch,
                onValueChange = { branch = it },
                label = { Text("Branch / Department") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- EMAIL FIELD ---
        OutlinedTextField(
            value = email,
            onValueChange = { input ->
                email = input.trim()
                emailError = email.isNotEmpty() && !isValidMontclairEmail(email)
            },
            label = { Text("Email (@montclair.edu)") },
            singleLine = true,
            isError = emailError,
            supportingText = {
                if (emailError) {
                    Text("Email must end with @montclair.edu")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- PASSWORD FIELD ---
        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                passwordMatchError = confirmPassword.isNotEmpty() && password != confirmPassword
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
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

        // --- CONFIRM PASSWORD FIELD ---
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { newValue ->
                confirmPassword = newValue
                passwordMatchError = confirmPassword.isNotEmpty() && password != confirmPassword
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            isError = passwordMatchError,
            supportingText = {
                if (passwordMatchError) {
                    Text("Passwords do not match")
                }
            },
            visualTransformation = if (confirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
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

        // --- REGISTER BUTTON ---
        Button(
            onClick = {
                val nameOk = isValidName(name)
                val idOk = isValidStudentId(studentId)
                val emailOk = isValidMontclairEmail(email)
                val passOk = isPasswordValid

                registerError = null
                nameError = !nameOk
                studentIdError = !idOk
                emailError = !emailOk
                passwordMatchError = !passOk

                if (!isFormValid) {
                    Toast.makeText(
                        context,
                        "Please fill details correctly before registering",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                isRegistering = true
                val e = email.trim()
                val p = password
                val role = if (isProfessor) "professor" else "student"

                Log.d("AUTH", "Registering email=$e as $role")

                auth.createUserWithEmailAndPassword(e, p)
                    .addOnSuccessListener {
                        val uid = auth.currentUser?.uid
                        if (uid == null) {
                            isRegistering = false
                            registerError = "Registered but UID missing"
                            return@addOnSuccessListener
                        }

                        // 🔥 UPDATED USER MAP WITH ROLE
                        val userMap = hashMapOf(
                            "name" to name.trim(),
                            "studentId" to studentId.trim(),
                            "email" to e,
                            "role" to role,
                            "professorId" to if (isProfessor) professorId.trim() else null,
                            "branch" to if (isProfessor) branch.trim() else null,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )

                        db.collection("users").document(uid).set(userMap)
                            .addOnSuccessListener {
                                auth.currentUser?.sendEmailVerification()
                                    ?.addOnSuccessListener {
                                        isRegistering = false
                                        Log.d("AUTH", "REGISTER+PROFILE OK uid=$uid")
                                        Toast.makeText(
                                            context,
                                            "Registered! Verification email sent.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        onBackToLoginClick()
                                    }
                                    ?.addOnFailureListener { ex ->
                                        isRegistering = false
                                        registerError = "Verification email failed: ${ex.message}"
                                    }
                            }
                            .addOnFailureListener { ex ->
                                isRegistering = false
                                registerError = "Profile save failed: ${ex.message}"
                            }
                    }
                    .addOnFailureListener { ex ->
                        isRegistering = false
                        registerError = ex.message ?: "Register failed"
                    }
            },
            enabled = isFormValid && !isRegistering,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isRegistering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(if (isRegistering) "Creating..." else "Register")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                if (!isRegistering) onBackToLoginClick()
            }
        ) {
            Text("Already have an account? Login")
        }
    }
}