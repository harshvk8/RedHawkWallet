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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    tapVm: TapToPayViewModel
) {
    val st by tapVm.state.collectAsState()

    // Load wallet + transactions when screen opens
    LaunchedEffect(Unit) {
        tapVm.loadDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.QR_ID) }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.Gray
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

            // ✅ REAL BALANCE from ViewModel
            // st.balanceText is like "Balance: $200.0"
            val balanceOnly = st.balanceText.replace("Balance:", "").trim()
            WalletCard(balance = balanceOnly)

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ TAP BUTTON (demo)
            Button(
                onClick = { tapVm.loadDashboard() },   // ✅ just refresh, no payment
                enabled = !st.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (st.loading) "Refreshing..." else "Refresh Balance")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap an NFC tag to pay $5",
                color = Color.Gray
            )

            st.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Transactions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // ✅ TRANSACTION LIST from ViewModel
            if (st.transactionsText.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Transactions Yet", color = Color.Gray)
                }
            } else {
                // simple text list for demo
                Text(st.transactionsText)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}