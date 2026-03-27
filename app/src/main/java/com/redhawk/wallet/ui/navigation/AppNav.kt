package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.screens.*
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNav(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateNext = {
                    // Start at Login for now
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = { role, uid ->
                    // ✅ This connects the Login Result to the correct Route
                    if (role == "professor") {
                        navController.navigate("${Routes.PROFESSOR_ID}/$uid") {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate("${Routes.STUDENT_DASHBOARD}/$uid") {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _ -> },
                onBackToLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // ✅ STUDENT DASHBOARD ROUTE
        composable(
            route = "${Routes.STUDENT_DASHBOARD}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""

            // Note: Replace with your actual ViewModel Factory if needed
            val tapVm: TapToPayViewModel = viewModel()

            DashboardScreen(
                navController = navController,
                tapVm = tapVm,
                role = "student",
                uid = uid
            )
        }

        // ✅ PROFESSOR ROUTE
        composable(
            route = "${Routes.PROFESSOR_ID}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""

            // You can use a specific Professor screen or the shared Dashboard
            val tapVm: TapToPayViewModel = viewModel()

            DashboardScreen(
                navController = navController,
                tapVm = tapVm,
                role = "professor",
                uid = uid
            )
        }

        composable(Routes.QR_ID) {
            QrIdScreen(navController = navController)
        }

        composable(Routes.EMAIL_VERIFICATION_PENDING) {
            EmailVerificationPendingScreen(
                onVerified = {
                    navController.navigate(Routes.LOGIN)
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}