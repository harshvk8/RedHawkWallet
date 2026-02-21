package com.redhawk.wallet.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.TransactionHistoryScreen

@Composable
fun AppNav() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("transactions") {
            TransactionHistoryScreen()
        }
    }
}