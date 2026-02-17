package com.redhawk.wallet.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
