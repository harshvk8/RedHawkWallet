package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redhawk.wallet.ui.components.AccountSelector

@Composable
fun TapToPayScreen(vm: TapToPayViewModel) {

    val st by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadDashboard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Account",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        AccountSelector(
            selectedAccount = st.selectedAccount,
            onAccountSelected = { vm.selectAccount(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(st.balanceText, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        if (!st.isEmailVerified) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3CD)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ Your email is not verified. Payments are disabled until you verify your email.",
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFF856404),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        val buttonText = when (st.selectedAccount) {
            com.redhawk.wallet.data.models.AccountType.MEAL_SWIPES -> "Simulate Tap (-1 Swipe)"
            else -> "Simulate Tap (-$5)"
        }

        Button(
            onClick = { vm.simulateTap() },
            enabled = !st.loading && st.isEmailVerified
        ) {
            Text(if (st.loading) "Processing..." else buttonText)
        }

        if (!st.isEmailVerified) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Payments disabled until email is verified.",
                color = Color(0xFFDC3545),
                style = MaterialTheme.typography.bodySmall
            )
        }

        st.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Transactions", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            if (st.transactionsText.isBlank())
                "No transactions yet"
            else
                st.transactionsText
        )
    }
}