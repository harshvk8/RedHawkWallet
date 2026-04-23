package com.redhawk.wallet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.components.AccountSelector
import com.redhawk.wallet.ui.components.WalletCard
import com.redhawk.wallet.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    tapVm: TapToPayViewModel
) {
    val st by tapVm.state.collectAsState()
    val scrollState = rememberScrollState()
    var showTransactions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tapVm.loadDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.QR_ID) }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Account",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AccountSelector(
                selectedAccount = st.selectedAccount,
                onAccountSelected = { tapVm.selectAccount(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            WalletCard(
                balance = st.balanceText,
                accountLabel = st.selectedAccount.displayName
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { tapVm.loadDashboard() },
                enabled = !st.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (st.loading) "Refreshing..." else "Refresh Balance")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (st.selectedAccount.name == "MEAL_SWIPES") {
                    "Tap an NFC tag to use 1 meal swipe"
                } else {
                    "Tap an NFC tag to pay $5"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            st.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTransactions = !showTransactions }
                    .padding(vertical = 14.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (showTransactions) {
                            Icons.Filled.KeyboardArrowDown
                        } else {
                            Icons.Filled.KeyboardArrowRight
                        },
                        contentDescription = "Toggle Transactions",
                        tint = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showTransactions) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (st.transactionsText.isBlank()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No Transactions Yet",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = st.transactionsText,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}