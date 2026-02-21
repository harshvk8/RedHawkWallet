package com.redhawk.wallet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TransactionItem(
    title: String,
    amount: String,
    date: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(title)
            Text(amount)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall
        )

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}