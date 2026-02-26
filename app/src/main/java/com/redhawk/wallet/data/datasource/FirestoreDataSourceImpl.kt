package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreDataSourceImpl {
    private val db = FirebaseFirestore.getInstance()

    suspend fun setDocument(path: String, data: Map<String, Any?>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = path.split("/")
                when (pathParts.size) {
                    2 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .set(data)
                            .await()
                        true
                    }
                    4 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .collection(pathParts[2])
                            .document(pathParts[3])
                            .set(data)
                            .await()
                        true
                    }
                    else -> false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun getDocument(path: String): DocumentSnapshot? {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = path.split("/")
                when (pathParts.size) {
                    2 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .get()
                            .await()
                    }
                    4 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .collection(pathParts[2])
                            .document(pathParts[3])
                            .get()
                            .await()
                    }
                    else -> null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun addDocument(collectionPath: String, data: Map<String, Any?>): String? {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = collectionPath.split("/")
                val docRef = when (pathParts.size) {
                    1 -> {
                        // Simple collection: "users"
                        db.collection(pathParts[0])
                            .add(data)
                            .await()
                    }
                    3 -> {
                        // Subcollection: "transactions/uid123/items"
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .collection(pathParts[2])
                            .add(data)
                            .await()
                    }
                    else -> return@withContext null
                }
                docRef.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getCollection(collectionPath: String): QuerySnapshot? {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = collectionPath.split("/")
                when (pathParts.size) {
                    1 -> {
                        // Simple collection: "users"
                        db.collection(pathParts[0])
                            .get()
                            .await()
                    }
                    3 -> {
                        // Subcollection: "transactions/uid123/items"
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .collection(pathParts[2])
                            .get()
                            .await()
                    }
                    else -> null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun updateFields(path: String, fields: Map<String, Any?>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = path.split("/")
                when (pathParts.size) {
                    2 -> {
                        // Document in collection: "users/uid123"
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .update(fields)
                            .await()
                        true
                    }
                    4 -> {
                        // Document in subcollection: "transactions/uid123/items/itemId"
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .collection(pathParts[2])
                            .document(pathParts[3])
                            .update(fields)
                            .await()
                        true
                    }
                    else -> false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deleteDocument(path: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = path.split("/")
                when (pathParts.size) {
                    2 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .delete()
                            .await()
                        true
                    }
                    4 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .collection(pathParts[2])
                            .document(pathParts[3])
                            .delete()
                            .await()
                        true
                    }
                    else -> false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = db.collection("users")
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .await()
                !result.isEmpty
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun checkStudentIdExists(studentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = db.collection("users")
                    .whereEqualTo("studentId", studentId)
                    .limit(1)
                    .get()
                    .await()
                !result.isEmpty
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun addTransactionToUser(uid: String, transaction: Map<String, Any?>): String? {
        return withContext(Dispatchers.IO) {
            try {
                val docRef = db.collection("transactions")
                    .document(uid)
                    .collection("items")
                    .add(transaction)
                    .await()
                docRef.id
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getUserTransactions(uid: String): QuerySnapshot? {
        return withContext(Dispatchers.IO) {
            try {
                db.collection("transactions")
                    .document(uid)
                    .collection("items")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun checkDocumentExists(path: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pathParts = path.split("/")
                val doc = when (pathParts.size) {
                    2 -> {
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .get()
                            .await()
                    }
                    else -> null
                }
                doc?.exists() ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
