package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

        Text(st.balanceText, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { vm.simulateTap() },
            enabled = !st.loading
        ) {
            Text(if (st.loading) "Processing..." else "Simulate Tap (-$5)")
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