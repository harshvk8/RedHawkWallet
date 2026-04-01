package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
import com.redhawk.wallet.feature_auth.AuthViewModel
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.EmailVerificationPendingScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.SplashScreen
import com.redhawk.wallet.ui.screens.TapToPayViewModel
import com.redhawk.wallet.ui.screens.TapToPayViewModelFactory

@Composable
fun AppNav(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val tapVm: TapToPayViewModel = viewModel(
        factory = TapToPayViewModelFactory(
            WalletRepository(FirestoreDataSource())
        )
    )

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

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                navController = navController,
                tapVm = tapVm
            )
        }

        composable(Routes.EMAIL_VERIFICATION) {
            EmailVerificationPendingScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}