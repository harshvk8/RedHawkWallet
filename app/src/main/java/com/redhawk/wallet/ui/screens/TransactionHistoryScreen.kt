package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redhawk.wallet.ui.screens.components.TransactionItem

@Composable
fun TransactionHistoryScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TransactionItem(
            title = "Starbucks",
            amount = "-$6.50",
            date = "Feb 20, 2026"
        )

        TransactionItem(
            title = "Bookstore",
            amount = "-$42.00",
            date = "Feb 19, 2026"
        )
    }
}