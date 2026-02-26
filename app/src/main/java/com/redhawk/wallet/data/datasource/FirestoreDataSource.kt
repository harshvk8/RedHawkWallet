package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreDataSource {
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
                        db.collection(pathParts[0])
                            .add(data)
                            .await()
                    }
                    3 -> {
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
                        db.collection(pathParts[0])
                            .get()
                            .await()
                    }
                    3 -> {
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
                        db.collection(pathParts[0])
                            .document(pathParts[1])
                            .update(fields)
                            .await()
                        true
                    }
                    4 -> {
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

    suspend fun checkEmailExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .await()
                !result.isEmpty
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun checkStudentIdExists(studentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = db.collection("users")
                    .whereEqualTo("studentId", studentId)
                    .get()
                    .await()
                !result.isEmpty
            } catch (e: Exception) {
                false
            }
        }
    }
}
