package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Offer

class OfferRepository(
    private val firestore: FirestoreDataSource
) {
    private val collectionPath = "offers"

    private fun offerPath(offerId: String) = "$collectionPath/$offerId"

    suspend fun getOffers(): List<Offer> {
        return firestore.getCollection(collectionPath, Offer::class.java)
    }

    suspend fun getOffer(offerId: String): Offer? {
        require(offerId.isNotBlank()) { "offerId cannot be blank" }
        return firestore.getDocument(offerPath(offerId), Offer::class.java)
    }

    suspend fun redeemOffer(offerId: String, uid: String) {
        require(offerId.isNotBlank()) { "offerId cannot be blank" }
        require(uid.isNotBlank()) { "uid cannot be blank" }

        firestore.setDocument(
            "users/$uid/redeemedOffers/$offerId",
            mapOf(
                "offerId" to offerId,
                "redeemedAt" to System.currentTimeMillis()
            )
        )
    }
}