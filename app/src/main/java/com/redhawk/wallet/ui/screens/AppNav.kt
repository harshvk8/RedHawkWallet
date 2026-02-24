package com.redhawk.wallet.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { email, password ->
                    // TODO: connect Firebase auth later.
                    // For now, navigate to dashboard.
                    navController.navigate(Routes.DASHBOARD)
                },
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { name, studentId, email, password ->
                    // TODO: connect Firebase register later.
                    // For now, go back to login after "register".
                    navController.popBackStack()
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(Routes.TRANSACTIONS) {
            TransactionHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}