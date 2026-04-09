package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.Event

class EventRepository(
    private val firestore: FirestoreDataSource
) {
    private val collectionPath = "events"
    private fun eventPath(eventId: String) = "$collectionPath/$eventId"

    suspend fun getEvents(): List<Event> {
        return firestore.getCollection(collectionPath, Event::class.java)
    }

    suspend fun getEvent(eventId: String): Event? {
        require(eventId.isNotBlank()) { "eventId cannot be blank" }
        return firestore.getDocument(eventPath(eventId), Event::class.java)
    }
}