package com.redhawk.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.TransactionScreen
import com.redhawk.wallet.ui.theme.RedHawkWalletTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RedHawkWalletTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // 🔐 Login Screen
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Temporary login success
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = {
                    // Future register screen
                }
            )
        }

        // 🏠 Dashboard Screen
        composable("dashboard") {
            DashboardScreen(
                userName = "Student",
                balance = 100.0,
                onPayClick = {
                    navController.navigate("transaction")
                },
                onQrClick = {
                    // Future QR screen
                },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        // 💳 Transaction Screen
        composable("transaction") {
            TransactionScreen(
                onPayClicked = { amount ->
                    println("Transaction paid: $amount")
                    navController.popBackStack()
                }
            )
        }
    }
}
