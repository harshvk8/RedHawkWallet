package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    userName: String = "Student",
    balance: Double = 0.0,
    onPayClick: () -> Unit,
    onQrClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome, $userName",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Wallet Balance")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onPayClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tap to Pay")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onQrClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show QR Code")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
