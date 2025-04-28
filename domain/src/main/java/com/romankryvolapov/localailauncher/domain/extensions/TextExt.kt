/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.extensions

import android.util.Base64
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import java.security.Key
import java.util.UUID

private const val TAG = "TextExtTag"

fun String.toBase64(): String {
    return try {
        Base64.encodeToString(toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    } catch (e: Exception) {
        logError("toBase64 Exception: ${e.message}", e, TAG)
        ""
    }
}

fun String.fromBase64(): String {
    return try {
        String(Base64.decode(this, Base64.NO_WRAP), charset("UTF-8"))
    } catch (e: Exception) {
        logError("toBase64 Exception: ${e.message}", e, TAG)
        ""
    }
}

fun Key.toBase64(): String {
    return try {
        Base64.encodeToString(encoded, Base64.NO_WRAP)
    } catch (e: Exception) {
        logError("toBase64 Exception: ${e.message}", e, TAG)
        ""
    }
}

fun ByteArray.toBase64(): String {
    return try {
        Base64.encodeToString(this, Base64.NO_WRAP)
    } catch (e: Exception) {
        logError("toBase64 Exception: ${e.message}", e, TAG)
        ""
    }
}

fun getDeviceUUID(): String {
    return UUID.randomUUID().toString()
}