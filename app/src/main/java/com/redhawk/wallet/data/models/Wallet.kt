package com.redhawk.wallet.data.models

data class Wallet(
    val uid: String = "",
    val balance: Double = 100.0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "balance" to balance,
            "lastUpdated" to lastUpdated,
            "isActive" to isActive
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Wallet {
            return Wallet(
                uid = map["uid"] as? String ?: "",
                balance = (map["balance"] as? Number)?.toDouble() ?: 100.0,
                lastUpdated = map["lastUpdated"] as? Long ?: System.currentTimeMillis(),
                isActive = map["isActive"] as? Boolean ?: true
            )
        }
    }
}
