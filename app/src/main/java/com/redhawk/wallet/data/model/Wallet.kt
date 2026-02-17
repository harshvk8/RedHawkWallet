package com.redhawk.wallet.data.model

data class Wallet(
    val uid: String = "",
    val balance: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)
