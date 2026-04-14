package com.redhawk.wallet.data.models

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val read: Boolean = false,
    val timestamp: Long = 0L
)