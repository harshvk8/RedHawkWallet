package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.RegisterScreen
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
import com.redhawk.wallet.ui.screens.TapToPayViewModel
@Composable
fun AppNav(navController: NavHostController) {

    val startDestination = remember {
        if (FirebaseAuth.getInstance().currentUser != null) Routes.DASHBOARD else Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSignUpClick = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { _, _, _, _ -> },
                onBackToLoginClick = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            val ds = remember { FirestoreDataSource() }
            val walletRepo = remember { WalletRepository(ds) }
            val tapVm = remember { TapToPayViewModel(walletRepo) }

            DashboardScreen(navController , tapVm)
        }

        //  QR / ID screen route
        composable(Routes.QR_ID) {
            QrIdScreen(navController = navController)
        }
    }
}