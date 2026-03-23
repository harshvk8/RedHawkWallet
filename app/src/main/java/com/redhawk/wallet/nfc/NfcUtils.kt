package com.redhawk.wallet.nfc

import android.nfc.NdefRecord
import java.nio.charset.Charset

object NfcUtils {

    fun ByteArray.toHexString(): String =
        joinToString("") { "%02X".format(it) }

    fun parseTextRecord(record: NdefRecord): String? {
        return try {
            if (record.tnf != NdefRecord.TNF_WELL_KNOWN) return null
            if (!record.type.contentEquals(NdefRecord.RTD_TEXT)) return null

            val payload = record.payload ?: return null
            if (payload.isEmpty()) return null

            val langLength = payload[0].toInt() and 0x3F
            val isUtf8 = (payload[0].toInt() and 0x80) == 0
            val charset = if (isUtf8) Charsets.UTF_8 else Charset.forName("UTF-16")

            String(payload, 1 + langLength, payload.size - 1 - langLength, charset)
        } catch (_: Exception) {
            null
        }
    }
}
