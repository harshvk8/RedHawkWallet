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
    tapVm: TapToPayViewModel
) {
    val st by tapVm.state.collectAsState()

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