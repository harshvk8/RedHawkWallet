package com.redhawk.wallet.data.datasource

interface FirestoreDataSource {

    suspend fun setDocument(path: String, data: Any)

    suspend fun <T> getDocument(path: String, clazz: Class<T>): T?

    suspend fun updateFields(path: String, fields: Map<String, Any?>)

    suspend fun addDocument(collectionPath: String, data: Any): String

    suspend fun <T> getCollection(collectionPath: String, clazz: Class<T>): List<T>
}
