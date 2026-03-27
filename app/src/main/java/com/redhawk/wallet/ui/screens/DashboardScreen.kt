package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.components.WalletCard
import com.redhawk.wallet.ui.navigation.Routes
import com.redhawk.wallet.viewmodels.TapToPayViewModel // Ensure correct import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    tapVm: TapToPayViewModel,
    role: String = "student", // ✅ Added role parameter
    uid: String = ""          // ✅ Added uid parameter
) {
    val st by tapVm.state.collectAsState()

    LaunchedEffect(uid) {
        tapVm.loadDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (role == "professor") "Professor Portal" else "Student Home") },
                actions = {
                    // Show QR ID only for students
                    if (role == "student") {
                        IconButton(onClick = { navController.navigate(Routes.QR_ID) }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "My QR ID",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        // Show Scanner icon for Professors
                        IconButton(onClick = { /* Navigate to Scanner if you have one */ }) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = "Scan Student",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 💳 UNIVERSAL: Balance Card
            val balanceOnly = st.balanceText.replace("Balance:", "").trim()
            WalletCard(balance = balanceOnly)

            Spacer(modifier = Modifier.height(16.dp))

            // 🔄 UNIVERSAL: Refresh Button
            Button(
                onClick = { tapVm.loadDashboard() },
                enabled = !st.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (st.loading) "Refreshing..." else "Refresh Balance")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🎭 ROLE-BASED UI SECTION
            when (role) {
                "professor" -> {
                    Text("Professor Tools", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { /* Future: Manage Class logic */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verify Student Attendance")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You are logged in as a Faculty member.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                "student" -> {
                    Text("Payments", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap an NFC tag to pay $5",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    st.error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📜 UNIVERSAL: Transactions
            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (st.transactionsText.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Transactions Yet", color = Color.Gray)
                }
            } else {
                // Assuming st.transactionsText is a placeholder for a list
                Text(st.transactionsText)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}