package com.redhawk.wallet.data.repository

import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.AccountType
import com.redhawk.wallet.data.models.Transactions
import com.redhawk.wallet.data.models.Wallet

class WalletRepository(
    private val firestore: FirestoreDataSource
) {

    suspend fun getWallet(uid: String): Wallet? {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getWallet(uid)
    }

    suspend fun initWallet(uid: String) {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        firestore.initWallet(uid)
    }

    suspend fun getBalanceForAccount(uid: String, accountType: AccountType): Double {
        val wallet = getWallet(uid) ?: return 0.0

        return when (accountType) {
            AccountType.RED_HAWK_DOLLARS -> wallet.redHawkDollars
            AccountType.FLEX -> wallet.flex
            AccountType.BONUS -> wallet.bonus
            AccountType.MEAL_SWIPES -> wallet.mealSwipes
        }
    }

    suspend fun getTransactions(uid: String): List<Transactions> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getLatestTransactions(uid, 100)
    }

    suspend fun getLatestTransactions(uid: String): List<Transactions> {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.getLatestTransactions(uid, 20)
    }

    suspend fun tapAndPay(uid: String, accountType: AccountType): Transactions {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        return firestore.tapAndPay(uid, accountType)
    }

    suspend fun tapAndPayWithToken(
        uid: String,
        token: String,
        accountType: AccountType
    ): Transactions {
        require(uid.isNotBlank()) { "uid cannot be blank" }
        require(token.isNotBlank()) { "token cannot be blank" }
        return firestore.tapAndPayWithToken(uid, token, accountType)
    }
}