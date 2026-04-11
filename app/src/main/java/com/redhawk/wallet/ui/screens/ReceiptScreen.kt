package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        horizontalAlignment = Alignment.CenterHorizontally//
    ) {
        Text(
            text = "Payment Receipt",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        ReceiptRow(label = "Merchant", value = merchant)
        ReceiptRow(label = "Amount", value = amount)
        ReceiptRow(label = "Date", value = date)
        ReceiptRow(label = "Transaction ID", value = transactionId)

        Spacer(modifier = Modifier.height(32.dp))

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
