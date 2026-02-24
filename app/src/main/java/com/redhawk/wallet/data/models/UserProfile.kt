package com.redhawk.wallet.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
