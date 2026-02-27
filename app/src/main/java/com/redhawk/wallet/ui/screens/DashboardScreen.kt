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

data class Transaction(
    val amount: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {

    // ✅ Transaction list (empty at start)
    val transactions = remember { mutableStateListOf<Transaction>() }

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
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            WalletCard(balance = "$0.00")

            Spacer(modifier = Modifier.height(24.dp))

            // 🔴 If no transactions
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Transactions Yet",
                        color = Color.Gray
                    )
                }
            } else {
                Column {
                    transactions.forEach {
                        Text("${it.description} - ${it.amount}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


        }
    }
}