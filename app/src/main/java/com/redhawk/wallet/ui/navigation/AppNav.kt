package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
import com.redhawk.wallet.feature_auth.AuthViewModel
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.qr.QrScannerScreen
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.EmailVerificationPendingScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.RegisterScreen
import com.redhawk.wallet.ui.screens.SplashScreen
import com.redhawk.wallet.ui.screens.TapToPayViewModel
import com.redhawk.wallet.ui.screens.TapToPayViewModelFactory

@Composable
fun AppNav(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateNext = {
                    val isLoggedIn = authViewModel.checkCurrentUser()
                    if (isLoggedIn) {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                            launchSingleTop = true
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
                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _ ->
                    navController.navigate(Routes.EMAIL_VERIFICATION) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            val tapVm: TapToPayViewModel = viewModel(
                factory = TapToPayViewModelFactory(
                    WalletRepository(FirestoreDataSource())
                )
            )

            DashboardScreen(
                navController = navController,
                tapVm = tapVm
            )
        }

        composable(Routes.EMAIL_VERIFICATION) {
            EmailVerificationPendingScreen(
                authViewModel = authViewModel,
                onVerified = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.EMAIL_VERIFICATION) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.EMAIL_VERIFICATION) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.QR_ID) {
            QrIdScreen(navController = navController)
        }

        composable(Routes.QR_SCANNER) {
            QrScannerScreen(navController = navController)
        }
    }
}