package com.redhawk.wallet.data.datasource

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.redhawk.wallet.data.models.AccountType
import com.redhawk.wallet.data.models.Transactions
import com.redhawk.wallet.data.models.UserProfile
import com.redhawk.wallet.data.models.Wallet
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun createUserProfile(uid: String, profile: UserProfile) {
        db.collection("users").document(uid).set(profile).await()
    }

    suspend fun setDocument(documentPath: String, data: Any) {
        db.document(documentPath).set(data).await()
    }

    suspend fun <T> getDocument(documentPath: String, clazz: Class<T>): T? {
        val snap = db.document(documentPath).get().await()
        return snap.toObject(clazz)
    }

    suspend fun initWallet(uid: String) {
        val wallet = Wallet(
            uid = uid,
            redHawkDollars = 200.0,
            flex = 100.0,
            bonus = 50.0,
            mealSwipes = 10.0,
            updatedAt = System.currentTimeMillis()
        )
        db.collection("wallets").document(uid).set(wallet).await()
    }

    suspend fun getWallet(uid: String): Wallet? {
        val snap = db.collection("wallets").document(uid).get().await()
        Log.d("WalletDebug", "wallet exists = ${snap.exists()}")
        Log.d("WalletDebug", "wallet data = ${snap.data}")

        if (!snap.exists()) return null

        return Wallet(
            uid = snap.getString("uid") ?: uid,
            redHawkDollars = readNumber(
                snap,
                "redHawkDollars",
                "redHawkWallet",
                "redHawkWallets",
                "Red Hawk Dollars"
            ),
            flex = readNumber(snap, "flex", "Flex"),
            bonus = readNumber(snap, "bonus", "Bonus"),
            mealSwipes = readNumber(snap, "mealSwipes", "Meal Swipes"),
            updatedAt = readLong(snap, "updatedAt")
        )
    }

    suspend fun updateDocument(documentPath: String, data: Map<String, Any>) {
        db.document(documentPath).update(data).await()
    }

    private fun getBalanceField(accountType: AccountType): String {
        return when (accountType) {
            AccountType.RED_HAWK_DOLLARS -> "redHawkDollars"
            AccountType.FLEX -> "flex"
            AccountType.BONUS -> "bonus"
            AccountType.MEAL_SWIPES -> "mealSwipes"
        }
    }

    private fun getDeductionAmount(accountType: AccountType): Double {
        return when (accountType) {
            AccountType.MEAL_SWIPES -> 1.0
            else -> 5.0
        }
    }

    private fun getAccountLabel(accountType: AccountType): String {
        return when (accountType) {
            AccountType.RED_HAWK_DOLLARS -> "Red Hawk Dollars / Debit"
            AccountType.FLEX -> "Flex"
            AccountType.BONUS -> "Bonus"
            AccountType.MEAL_SWIPES -> "Meal Swipes"
        }
    }

    private fun readNumber(snapshot: DocumentSnapshot, vararg fieldNames: String): Double {
        for (field in fieldNames) {
            val value = snapshot.get(field)
            val parsed = when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull()
                else -> null
            }
            if (parsed != null) return parsed
        }
        return 0.0
    }

    private fun readLong(snapshot: DocumentSnapshot, fieldName: String): Long {
        val value = snapshot.get(fieldName)
        return when (value) {
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is Float -> value.toLong()
            is Number -> value.toLong()
            is String -> value.toLongOrNull() ?: 0L
            else -> 0L
        }
    }

    suspend fun tapAndPayWithToken(
        uid: String,
        token: String,
        accountType: AccountType,
        amountToDeduct: Double = getDeductionAmount(accountType)
    ): Transactions {
        val walletRef = db.collection("wallets").document(uid)
        val txId = UUID.randomUUID().toString()
        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document(txId)

        val balanceField = getBalanceField(accountType)

        return db.runTransaction { transaction ->
            val walletSnap = transaction.get(walletRef)
            val currentBalance = when (accountType) {
                AccountType.RED_HAWK_DOLLARS -> readNumber(
                    walletSnap,
                    "redHawkDollars",
                    "redHawkWallet",
                    "redHawkWallets",
                    "Red Hawk Dollars"
                )
                AccountType.FLEX -> readNumber(walletSnap, "flex", "Flex")
                AccountType.BONUS -> readNumber(walletSnap, "bonus", "Bonus")
                AccountType.MEAL_SWIPES -> readNumber(walletSnap, "mealSwipes", "Meal Swipes")
            }

            Log.d("TapDebug", "tapAndPayWithToken accountType = $accountType")
            Log.d("TapDebug", "tapAndPayWithToken balanceField = $balanceField")
            Log.d("TapDebug", "tapAndPayWithToken currentBalance = $currentBalance")

            if (currentBalance < amountToDeduct) {
                throw IllegalStateException("Insufficient ${getAccountLabel(accountType)} balance")
            }

            val newBalance = currentBalance - amountToDeduct
            Log.d("TapDebug", "tapAndPayWithToken newBalance = $newBalance")

            transaction.update(
                walletRef,
                mapOf(
                    balanceField to newBalance,
                    "updatedAt" to System.currentTimeMillis()
                )
            )

            val tx = Transactions(
                id = txId,
                uid = uid,
                token = token,
                amount = amountToDeduct,
                status = "success",
                type = accountType.name.lowercase(),
                note = "Paid via NFC tap using ${getAccountLabel(accountType)}",
                timestamp = System.currentTimeMillis()
            )

            transaction.set(txRef, tx)
            tx
        }.await()
    }

    suspend fun tapAndPay(
        uid: String,
        accountType: AccountType,
        amountToDeduct: Double = getDeductionAmount(accountType)
    ): Transactions {
        val walletRef = db.collection("wallets").document(uid)
        val txId = UUID.randomUUID().toString()
        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document(txId)

        val balanceField = getBalanceField(accountType)

        return db.runTransaction { transaction ->
            val walletSnap = transaction.get(walletRef)
            val currentBalance = when (accountType) {
                AccountType.RED_HAWK_DOLLARS -> readNumber(
                    walletSnap,
                    "redHawkDollars",
                    "redHawkWallet",
                    "redHawkWallets",
                    "Red Hawk Dollars"
                )
                AccountType.FLEX -> readNumber(walletSnap, "flex", "Flex")
                AccountType.BONUS -> readNumber(walletSnap, "bonus", "Bonus")
                AccountType.MEAL_SWIPES -> readNumber(walletSnap, "mealSwipes", "Meal Swipes")
            }

            Log.d("TapDebug", "tapAndPay accountType = $accountType")
            Log.d("TapDebug", "tapAndPay balanceField = $balanceField")
            Log.d("TapDebug", "tapAndPay currentBalance = $currentBalance")

            if (currentBalance < amountToDeduct) {
                throw IllegalStateException("Insufficient ${getAccountLabel(accountType)} balance")
            }

            val newBalance = currentBalance - amountToDeduct
            Log.d("TapDebug", "tapAndPay newBalance = $newBalance")

            transaction.update(
                walletRef,
                mapOf(
                    balanceField to newBalance,
                    "updatedAt" to System.currentTimeMillis()
                )
            )

            val token = UUID.randomUUID().toString()

            val tx = Transactions(
                id = txId,
                uid = uid,
                token = token,
                amount = amountToDeduct,
                status = "success",
                type = accountType.name.lowercase(),
                note = "Demo tap payment using ${getAccountLabel(accountType)}",
                timestamp = System.currentTimeMillis()
            )

            transaction.set(txRef, tx)
            tx
        }.await()
    }

    suspend fun getLatestTransactions(
        uid: String,
        limit: Long = 20
    ): List<Transactions> {
        val snap = db.collection("users")
            .document(uid)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.toObjects(Transactions::class.java)
    }

    suspend fun addDocument(collectionPath: String, data: Any): String {
        val ref = db.collection(collectionPath).add(data).await()
        return ref.id
    }

    suspend fun <T> getCollection(
        collectionPath: String,
        clazz: Class<T>
    ): List<T> {
        val snap = db.collection(collectionPath).get().await()
        return snap.toObjects(clazz)
    }
}