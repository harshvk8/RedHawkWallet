package com.redhawk.wallet.data.models

data class Wallet(
    val uid: String = "",
    val redHawkDollars: Double = 200.0,
    val flex: Double = 100.0,
    val bonus: Double = 50.0,
    val mealSwipes: Double = 10.0,
    val updatedAt: Long = 0L
)