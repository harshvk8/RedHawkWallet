package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.screens.components.WalletCard

@Composable
fun DashboardScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        WalletCard(balance = "$120.00")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("transactions")
        }) {
            Text("View Transactions")
        }
    }
}