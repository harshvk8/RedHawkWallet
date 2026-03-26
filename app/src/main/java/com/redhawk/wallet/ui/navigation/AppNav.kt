package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.EmailVerificationPendingScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.RegisterScreen
import com.redhawk.wallet.ui.screens.SplashScreen
import com.redhawk.wallet.ui.screens.TapToPayViewModel

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
                onRegisterClick = { _, _, _, _ ->
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBackToLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            val tapVm: TapToPayViewModel = viewModel()
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