package com.redhawk.wallet.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreDataSource {

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val USERS = "users"
        const val WALLETS = "wallets"
        const val TRANSACTIONS = "transactions"
    }

    fun setDocument(
        path: String,
        data: Any,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.document(path)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun getDocument(
        path: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.document(path)
            .get()
            .addOnSuccessListener { onSuccess(it.data) }
            .addOnFailureListener { onError(it) }
    }

    fun addDocument(
        path: String,
        data: Any,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection(path)
            .add(data)
            .addOnSuccessListener { onSuccess(it.id) }
            .addOnFailureListener { onError(it) }
    }

    fun getCollection(
        path: String,
        onSuccess: (QuerySnapshot) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection(path)
            .get()
            .addOnSuccessListener { onSuccess(it) }
            .addOnFailureListener { onError(it) }
    }
}
