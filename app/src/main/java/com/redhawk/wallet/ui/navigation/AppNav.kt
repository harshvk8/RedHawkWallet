package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.RegisterScreen
import com.redhawk.wallet.ui.screens.SplashScreen
import com.redhawk.wallet.ui.screens.TapToPayViewModel

@Composable
fun AppNav(
    navController: NavHostController
) {
    val tapVm: TapToPayViewModel = viewModel()

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
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _ -> },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                navController = navController,
                tapVm = tapVm
            )
        }

        composable(Routes.QR_ID) {
            QrIdScreen(navController = navController)
        }
    }
}