package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun <T> getCollection(
        collectionPath: String,
        clazz: Class<T>
    ): List<T> {
        return firestore.collection(collectionPath)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(clazz) }
    }

    suspend fun <T> getDocument(
        documentPath: String,
        clazz: Class<T>
    ): T? {
        return firestore.document(documentPath)
            .get()
            .await()
            .toObject(clazz)
    }

    suspend fun updateDocument(
        documentPath: String,
        data: Map<String, Any>
    ) {
        firestore.document(documentPath)
            .update(data)
            .await()
    }

    suspend fun setDocument(
        documentPath: String,
        data: Any
    ) {
        firestore.document(documentPath)
            .set(data)
            .await()
    }
}