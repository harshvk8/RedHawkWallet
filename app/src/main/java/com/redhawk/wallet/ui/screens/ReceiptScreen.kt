package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redhawk.wallet.ui.components.PrimaryButton

@Composable
fun ReceiptScreen(
    amount: String,
    merchant: String,
    date: String,
    transactionId: String,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Payment Receipt",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        ReceiptRow(label = "Merchant", value = merchant)
        ReceiptRow(label = "Amount", value = amount)
        ReceiptRow(label = "Date", value = date)
        ReceiptRow(label = "Transaction ID", value = transactionId)

        Spacer(Modifier.height(32.dp))

        PrimaryButton(
            text = "Done",
            onClick = onDoneClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
