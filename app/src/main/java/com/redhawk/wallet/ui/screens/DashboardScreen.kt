package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.screens.components.TransactionItem
import com.redhawk.wallet.ui.screens.components.WalletCard

@Composable
fun DashboardScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        WalletCard(
            balance = "$2,450",
            cardHolder = "Ati"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recent Transactions")

        TransactionItem("Starbucks", "-$5.99", "Feb 20")
        TransactionItem("Amazon", "-$120", "Feb 18")
    }
}