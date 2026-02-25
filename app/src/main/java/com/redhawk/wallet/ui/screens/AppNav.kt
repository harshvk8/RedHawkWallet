package com.redhawk.wallet

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // TODO: replace with your real screen composable
            // Example: HomeScreen(navController)
        }

        composable("qr") {
            // TODO: replace with your real QR screen
        }

        composable("nfc") {
            // TODO: replace with your real NFC screen
        }
    }
}