package com.redhawk.wallet.nfc

data class SpendRequest(
    val tokenId: String,
    val userId: String,
    val amountCents: Int,
    val timestamp: Long,
    val signature: String

)

