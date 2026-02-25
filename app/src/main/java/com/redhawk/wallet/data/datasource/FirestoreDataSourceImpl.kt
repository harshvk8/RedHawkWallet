package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreDataSourceImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FirestoreDataSource {

    override suspend fun setDocument(path: String, data: Any) {
        require(path.isNotBlank()) { "path cannot be blank" }
        firestore.document(path).set(data).await()
    }

    override suspend fun <T> getDocument(path: String, clazz: Class<T>): T? {
        require(path.isNotBlank()) { "path cannot be blank" }
        val snap = firestore.document(path).get().await()
        return if (snap.exists()) snap.toObject(clazz) else null
    }

    override suspend fun updateFields(path: String, fields: Map<String, Any?>) {
        require(path.isNotBlank()) { "path cannot be blank" }
        require(fields.isNotEmpty()) { "fields cannot be empty" }
        firestore.document(path).update(fields).await()
    }

    override suspend fun addDocument(collectionPath: String, data: Any): String {
        require(collectionPath.isNotBlank()) { "collectionPath cannot be blank" }
        val docRef = firestore.collection(collectionPath).add(data).await()
        return docRef.id
    }

    override suspend fun <T> getCollection(collectionPath: String, clazz: Class<T>): List<T> {
        require(collectionPath.isNotBlank()) { "collectionPath cannot be blank" }
        val snap = firestore.collection(collectionPath).get().await()
        return snap.documents.mapNotNull { it.toObject(clazz) }
    }

    override suspend fun deleteDocument(documentPath: String) {
        require(documentPath.isNotBlank()) { "documentPath cannot be blank" }
        firestore.document(documentPath).delete().await()
    }
}