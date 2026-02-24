package com.redhawk.wallet.data.models

data class Transaction(
    val transactionId: String = "",
    val walletId: String = "",
    val amount: Double = 0.0,
    val type: String = ""
)
