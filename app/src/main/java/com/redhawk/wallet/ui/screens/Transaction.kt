package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TransactionScreen(
    onPayClicked: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Make Payment",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter Amount") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val value = amount.toDoubleOrNull()
                if (value != null) {
                    onPayClicked(value)
                }
            }
        ) {
            Text("Tap to Pay")
        }
    }
}
