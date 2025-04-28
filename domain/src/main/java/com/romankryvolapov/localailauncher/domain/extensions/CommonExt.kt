/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.extensions

import com.romankryvolapov.localailauncher.domain.models.base.TypeEnum
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

inline fun <reified T : Enum<T>> getEnumValue(type: String): T? {
    val values = enumValues<T>()
    return values.firstOrNull {
        it is TypeEnum && (it as TypeEnum).type.equals(type, true)
    }
}

@Throws(NoSuchAlgorithmException::class)
fun String.sha256(): String? {
    return try {
        val bytes = this.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val result = StringBuilder()
        for (byte in digest) {
            result.append(String.format("%02x", byte))
        }
        result.toString()
    } catch (e: Exception) {
        logError("sha256() Exception: ${e.message}", e, "sha256Tag")
        null
    }
}

fun String.capitalized(): String {
    return this.substring(0, 1).uppercase() + this.substring(1).lowercase();
}