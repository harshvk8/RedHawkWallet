package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.ui.components.WalletCard
import com.redhawk.wallet.ui.navigation.Routes

enum class AccountType(val label: String) {
    RED_HAWK_DOLLARS("Red Hawk Dollars"),
    FLEX("Flex"),
    BONUS("Bonus"),
    MEAL_SWIPES("Meal Swipes")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    tapVm: TapToPayViewModel
) {
    val st by tapVm.state.collectAsState()
    var selectedAccount by remember { mutableStateOf(AccountType.RED_HAWK_DOLLARS) }

    LaunchedEffect(Unit) {
        tapVm.loadDashboard()
    }

    val selectedBalance: String = when (selectedAccount) {
        AccountType.RED_HAWK_DOLLARS -> st.balanceText.replace("Balance:", "").trim()
        AccountType.FLEX             -> "$0.00"
        AccountType.BONUS            -> "$0.00"
        AccountType.MEAL_SWIPES      -> "0 swipes"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.QR_ID) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.Gray
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ScrollableTabRow(
                selectedTabIndex = AccountType.entries.indexOf(selectedAccount),
                edgePadding = 0.dp
            ) {
                AccountType.entries.forEach { type ->
                    Tab(
                        selected = selectedAccount == type,
                        onClick = { selectedAccount = type },
                        text = { Text(type.label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            WalletCard(
                balance = selectedBalance,
                accountLabel = selectedAccount.label
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
                text = "Tap an NFC tag to pay $5",
                color = Color.Gray
            )

            st.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Transactions", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (st.transactionsText.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Transactions Yet", color = Color.Gray)
                }
            } else {
                Text(st.transactionsText)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}