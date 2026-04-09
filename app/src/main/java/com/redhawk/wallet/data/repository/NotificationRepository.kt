package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.NotificationItem

class NotificationRepository(
    private val firestore: FirestoreDataSource
) {
    private fun notifCollectionPath(uid: String) = "users/$uid/notifications"
    private fun notifPath(uid: String, notifId: String) = "${notifCollectionPath(uid)}/$notifId"

    suspend fun getNotifications(uid: String): List<NotificationItem> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getCollection(notifCollectionPath(uid), NotificationItem::class.java)
    }

    suspend fun markAsRead(uid: String, notifId: String) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        require(notifId.isNotBlank()) { "notifId cannot be blank" }
        firestore.updateDocument(notifPath(uid, notifId), mapOf("read" to true))
    }
}