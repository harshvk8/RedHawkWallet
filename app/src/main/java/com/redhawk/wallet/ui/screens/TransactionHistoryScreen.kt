package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redhawk.wallet.ui.components.TransactionItem

@Composable
fun TransactionHistoryScreen(
    onBack: () -> Unit
) {
    val transactions = listOf(
        Transaction("Campus Store", "-$5.00", "Feb 18, 2026"),
        Transaction("Cafeteria", "-$8.50", "Feb 17, 2026"),
        Transaction("Printing Services", "-$2.00", "Feb 16, 2026"),
        Transaction("Top Up", "+$50.00", "Feb 15, 2026")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    title = transaction.title,
                    amount = transaction.amount,
                    date = transaction.date
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

data class Transaction(
    val title: String,
    val amount: String,
    val date: String
)