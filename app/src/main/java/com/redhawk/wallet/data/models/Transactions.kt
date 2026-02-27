package com.redhawk.wallet.data.models

data class Transactions(
    val id: String = "",
    val uid: String = "",
    val token: String = "",
    val amount: Double = 0.0,
    val status: String = "success",
    val type: String = "tap",
    val note: String = "test",
    val timestamp: Long = System.currentTimeMillis()
)