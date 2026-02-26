package com.redhawk.wallet.data

import com.redhawk.wallet.data.datasource.FirestoreDataSourceImpl
import com.redhawk.wallet.data.models.Transaction
import com.redhawk.wallet.data.models.UserProfile
import com.redhawk.wallet.data.models.Wallet

class TestDataGenerator {
    private val dataSource = FirestoreDataSourceImpl()

    suspend fun generateDemoUsers() {
        val teammates = listOf(
            UserProfile("harsh_uid", "Harsh", "nimeshh1@montclair.edu", "H001", role = "student"),
            UserProfile("ati_uid", "Ati", "karahalilh1@montclair.edu", "A002", role = "student"),
            UserProfile("rohaifa_uid", "Rohaifa", "yassinr1@montclair.edu", "R003", role = "student"),
            UserProfile("lisandra_uid", "Lisandra", "ninarosal1@montclair.edu", "L004", role = "student"),
            UserProfile("danilo_uid", "Danilo", "shotad1@montclair.edu", "D005", role = "student"),
            UserProfile("skerdi_uid", "Skerdi", "bekollaris1@montclair.edu", "S006", role = "student")
        )

        teammates.forEach { user ->
            // Save user profile
            dataSource.setDocument("users/${user.uid}", user.toMap())

            // Create wallet with $100 balance
            val wallet = Wallet(uid = user.uid, balance = 100.0)
            dataSource.setDocument("wallets/${user.uid}", wallet.toMap())

            // Add a sample transaction
            val transaction = Transaction(
                transactionId = "demo_${user.uid}_001",
                uid = user.uid,
                token = "TOKEN_${System.currentTimeMillis()}",
                amount = 5.0,
                type = "payment",
                status = "success",
                description = "Demo transaction"
            )
            dataSource.addTransactionToUser(user.uid, transaction.toMap())
        }
    }
}