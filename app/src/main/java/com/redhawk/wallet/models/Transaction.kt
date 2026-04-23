package com.redhawk.wallet.data.models

data class Transaction(
    val id: String = "",
    val uid: String = "",
    val token: String = "",
    val amount: Double = 0.0,
    val status: String = "",
    val type: String = "",
    val note: String = "",
    val timestamp: Long = 0L
)