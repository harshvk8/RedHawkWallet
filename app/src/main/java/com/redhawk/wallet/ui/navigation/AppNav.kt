package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.ui.screens.*

@Composable
fun AppNav(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = { role, uid ->
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

        composable(
            "${Routes.STUDENT_DASHBOARD}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) {
            val uid = it.arguments?.getString("uid") ?: ""
            val tapVm: TapToPayViewModel = viewModel()

            DashboardScreen(
                navController = navController,
                tapVm = tapVm,
                role = "student",
                uid = uid
            )
        }

        composable(
            "${Routes.PROFESSOR_ID}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) {
            val uid = it.arguments?.getString("uid") ?: ""
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

        composable(Routes.PROFESSOR_SCANNER) {
            ProfessorScannerScreen(navController = navController)
        }

        composable(
            "${Routes.STUDENT_VERIFY_RESULT}/{studentUid}",
            arguments = listOf(navArgument("studentUid") { type = NavType.StringType })
        ) {
            val studentUid = it.arguments?.getString("studentUid") ?: ""
            StudentVerificationResultScreen(navController, studentUid)
        }

        composable(Routes.EMAIL_VERIFICATION_PENDING) {
            EmailVerificationPendingScreen(
                onVerified = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.EMAIL_VERIFICATION_PENDING) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        composable(Routes.QR_SCANNER) {
                            QrScannerScreen(navController)
                        }
                    }
                }
            )
        }
    }
}