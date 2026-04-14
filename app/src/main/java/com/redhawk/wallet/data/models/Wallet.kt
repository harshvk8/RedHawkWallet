package com.redhawk.wallet.data.models

data class Wallet(
    val id: String = "main",
    val balance: Double = 0.0,
    val points: Int = 0
)