package com.redhawk.wallet.data.models

data class Offer(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pointsRequired: Int = 0,
    val expiryDate: String = "",
    val imageUrl: String = ""
)