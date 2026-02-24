package com.redhawk.wallet.nfc

data class Transaction( val token: String,
                        val timestamp: Long = System.currentTimeMillis()
                   )
