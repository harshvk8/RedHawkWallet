package com.redhawk.wallet.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Parcelable

class NfcReader {

    fun readFromIntent(intent: Intent?): NfcResult {
        if (intent == null) return NfcResult.Error("Null intent")

        val action = intent.action
        if (
            action != NfcAdapter.ACTION_TAG_DISCOVERED &&
            action != NfcAdapter.ACTION_TECH_DISCOVERED &&
            action != NfcAdapter.ACTION_NDEF_DISCOVERED
        ) {
            return NfcResult.Error("Not an NFC intent")
        }

        readNdefText(intent)?.let { return NfcResult.Success(it) }

        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        val tagId = tag?.id?.let { NfcUtils.run { it.toHexString() } }

        return if (!tagId.isNullOrBlank()) {
            NfcResult.Success(tagId)
        } else {
            NfcResult.Error("Failed to read NFC token")
        }
    }

    private fun readNdefText(intent: Intent): String? {
        val rawMsgs: Array<Parcelable>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES,
                    Parcelable::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            }

        val msg = rawMsgs?.firstOrNull() as? NdefMessage ?: return null

        for (record in msg.records) {
            val parsed = NfcUtils.parseTextRecord(record)
            if (!parsed.isNullOrBlank()) return parsed
        }
        return null
    }
}