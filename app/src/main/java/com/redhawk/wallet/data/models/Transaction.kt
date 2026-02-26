package com.redhawk.wallet.data.models

data class Transaction(
    val transactionId: String = "",
    val uid: String = "",
    val token: String = "",
    val amount: Double = 0.0,
    val type: String = "payment",
    val status: String = "success",
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "transactionId" to transactionId,
            "uid" to uid,
            "token" to token,
            "amount" to amount,
            "type" to type,
            "status" to status,
            "timestamp" to timestamp,
            "description" to description
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Transaction {
            return Transaction(
                transactionId = map["transactionId"] as? String ?: "",
                uid = map["uid"] as? String ?: "",
                token = map["token"] as? String ?: "",
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                type = map["type"] as? String ?: "payment",
                status = map["status"] as? String ?: "success",
                timestamp = map["timestamp"] as? Long ?: System.currentTimeMillis(),
                description = map["description"] as? String ?: ""
            )
        }
    }
}
