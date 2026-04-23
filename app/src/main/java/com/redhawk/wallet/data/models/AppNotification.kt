package com.redhawk.wallet.data.models

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Long = 0L,
    val read: Boolean = false
)