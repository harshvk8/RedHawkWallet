package com.redhawk.wallet.data.model

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
