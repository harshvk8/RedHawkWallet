package com.redhawk.wallet.nfc

data class OfflineToken(
    val tokenId: String,          // UUID
    val userId: String,           // uid
    val amountCents: Int,         // 200 = $2.00
    val issuedAt: Long,           // epoch ms
    val expiresAt: Long,          // epoch ms
    val signature: String         // server signature (or mock for now)
)

