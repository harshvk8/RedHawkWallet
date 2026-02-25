package com.redhawk.wallet.nfc

data class NfcTransaction(val token: String,
                          val timestamp: Long = System.currentTimeMillis()
                   )
