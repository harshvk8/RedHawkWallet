package com.redhawk.wallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.EventRepository
import com.redhawk.wallet.data.repository.OfferRepository
import com.redhawk.wallet.data.repository.WalletRepository
import com.redhawk.wallet.events.EventsOffersViewModel
import com.redhawk.wallet.events.EventsOffersViewModelFactory
import com.redhawk.wallet.feature_auth.AuthResult
import com.redhawk.wallet.feature_auth.AuthViewModel
import com.redhawk.wallet.qr.QrIdScreen
import com.redhawk.wallet.qr.QrScannerScreen
import com.redhawk.wallet.ui.screens.DashboardScreen
import com.redhawk.wallet.ui.screens.EmailVerificationPendingScreen
import com.redhawk.wallet.ui.screens.EventDetailsScreen
import com.redhawk.wallet.ui.screens.EventsOffersScreen
import com.redhawk.wallet.ui.screens.LoginScreen
import com.redhawk.wallet.ui.screens.OfferDetailsScreen
import com.redhawk.wallet.ui.screens.RegisterScreen
import com.redhawk.wallet.ui.screens.SplashScreen
import com.redhawk.wallet.ui.screens.TapToPayViewModel
import com.redhawk.wallet.ui.screens.TapToPayViewModelFactory

@Composable
fun AppNav(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val firestoreDataSource = FirestoreDataSource()

    val eventsOffersFactory = EventsOffersViewModelFactory(
        eventRepository = EventRepository(firestoreDataSource),
        offerRepository = OfferRepository(firestoreDataSource)
    )

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateNext = {
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    when {
                        currentUser == null -> {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        currentUser.isEmailVerified -> {
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        else -> {
                            navController.navigate(Routes.EMAIL_VERIFICATION) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (currentUser != null && currentUser.isEmailVerified) {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Routes.EMAIL_VERIFICATION) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
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
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthResult.Success -> {
                        authViewModel.clearState()
                        navController.navigate(Routes.EMAIL_VERIFICATION) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    else -> Unit
                }
            }

            RegisterScreen(
                onRegisterClick = { name, universityId, email, password, role ->
                    authViewModel.register(
                        name = name,
                        universityId = universityId,
                        email = email,
                        password = password,
                        role = role
                    )
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

        composable(Routes.EVENTS_OFFERS) {
            val vm: EventsOffersViewModel = viewModel(factory = eventsOffersFactory)

            EventsOffersScreen(
                navController = navController,
                viewModel = vm
            )
        }

        composable(
            route = Routes.EVENT_DETAILS,
            arguments = listOf(
                navArgument(Routes.EVENT_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString(Routes.EVENT_ID_ARG).orEmpty()
            val vm: EventsOffersViewModel = viewModel(factory = eventsOffersFactory)

            EventDetailsScreen(
                navController = navController,
                eventId = eventId,
                viewModel = vm
            )
        }

        composable(
            route = Routes.OFFER_DETAILS,
            arguments = listOf(
                navArgument(Routes.OFFER_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString(Routes.OFFER_ID_ARG).orEmpty()
            val vm: EventsOffersViewModel = viewModel(factory = eventsOffersFactory)

            OfferDetailsScreen(
                navController = navController,
                offerId = offerId,
                viewModel = vm
            )
        }
    }
}