package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
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
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (currentUser != null) {
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
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
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
                        launchSingleTop = true
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
            QrIdScreen(navController = navController)
        }

        composable(Routes.EMAIL_VERIFICATION_PENDING) {
            EmailVerificationPendingScreen(
                onVerified = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.EMAIL_VERIFICATION_PENDING) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}