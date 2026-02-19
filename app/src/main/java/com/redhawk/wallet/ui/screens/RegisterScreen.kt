package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.redhawk.wallet.R

private fun isValidName(name: String): Boolean {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return false
    return trimmed.all { it.isLetter() || it == ' ' }
}

private fun isValidMontclairEmail(email: String): Boolean {
    val trimmed = email.trim()
    return trimmed.endsWith("@montclair.edu", ignoreCase = true) && trimmed.contains("@")
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
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var studentIdError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.redhawk_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(20.dp))

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
                if (nameError) Text("Name must contain letters only")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

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

        Spacer(Modifier.height(12.dp))

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
                if (emailError) Text("Email must end with @montclair.edu")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = confirmPassword.isNotEmpty() && password != confirmPassword
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordError = confirmPassword.isNotEmpty() && password != confirmPassword
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError,
            supportingText = {
                if (passwordError) Text("Passwords do not match")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val nameOk = isValidName(name)
                val idOk = isValidStudentId(studentId)
                val emailOk = isValidMontclairEmail(email)
                val passOk = password.isNotEmpty() && password == confirmPassword

                nameError = !nameOk
                studentIdError = !idOk
                emailError = !emailOk
                passwordError = !passOk

                if (nameOk && idOk && emailOk && passOk) {
                    onRegisterClick(name.trim(), studentId.trim(), email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onBackToLoginClick) {
            Text("Already have an account? Login")
        }
    }
}
