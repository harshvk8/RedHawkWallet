package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.components.WalletCard
import com.redhawk.wallet.ui.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    tapVm: TapToPayViewModel,
    role: String = "student",
    uid: String = ""
) {
    val st by tapVm.state.collectAsState()

    LaunchedEffect(uid) {
        tapVm.loadDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (role == "professor") "Professor Portal" else "Student Home")
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (role == "student") {
                                navController.navigate(Routes.QR_ID)
                            } else {
                                navController.navigate(Routes.PROFESSOR_SCANNER)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = if (role == "student") "My QR ID" else "Scan Student QR",
                            tint = MaterialTheme.colorScheme.primary
                        )
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

            val balanceOnly = st.balanceText.replace("Balance:", "").trim()
            WalletCard(balance = balanceOnly)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { tapVm.loadDashboard() },
                enabled = !st.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (st.loading) "Refreshing..." else "Refresh Balance")
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (role) {
                "professor" -> {
                    Text("Professor Tools", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { navController.navigate(Routes.PROFESSOR_SCANNER) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Scan Student QR")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Use this to verify student identity or attendance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                else -> {
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
                Text(st.transactionsText)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}