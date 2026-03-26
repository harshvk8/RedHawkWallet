package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.screens.*
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository

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
                    val isLoggedIn = false

                    if (isLoggedIn) {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
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

        composable(Routes.DASHBOARD) {

            val tapVm = remember {
                TapToPayViewModel(
                    WalletRepository(FirestoreDataSource())
                )
            }

            DashboardScreen(
                navController = navController,
                tapVm = tapVm
            )
        }

        composable(Routes.QR_ID) {
            QrIdScreen(
                navController = navController
            )
        }

        composable(Routes.EMAIL_VERIFICATION_PENDING) {
            EmailVerificationPendingScreen(
                onVerified = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.EMAIL_VERIFICATION_PENDING) { inclusive = true }
                    }
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