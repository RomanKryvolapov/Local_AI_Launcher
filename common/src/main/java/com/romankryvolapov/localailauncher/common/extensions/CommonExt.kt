/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.common.extensions

import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.TypeEnum
import com.romankryvolapov.localailauncher.common.models.common.TypeEnumInt
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

inline fun <reified T : Enum<T>> getEnumValue(type: String): T? {
    val values = enumValues<T>()
    return values.firstOrNull {
        it is TypeEnum && (it as TypeEnum).type.equals(type, true)
    }
}

inline fun <reified T : Enum<T>> getEnumIntValue(type: Int): T? {
    val values = enumValues<T>()
    return values.firstOrNull {
        it is TypeEnumInt && (it as TypeEnumInt).type == type
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

fun <T> List<T>.nextOrFirst(current: T): T {
    val index = indexOf(current)
    if (index == -1) return current
    return if (index + 1 < size) this[index + 1] else this.first()
}

fun <T> List<T>.nextOrFirstOrCurrent(current: T): T {
    val index = indexOf(current)
    if (index == -1) return current
    return if (size == 1) current else if (index + 1 < size) this[index + 1] else this.first()
}

fun <K, V> Map<K, V>.print(
    tag: String,
    dropAfter: Int = 100,
) {
    if (isEmpty()) {
        logError("Map is empty", tag)
        return
    }
    for ((key, value) in entries) {
        val keyString = key.toString()
            .replace(Regex("[\r\n]+"), " ")
            .replace(Regex(" {2}"), " ")
        val valueString = value?.toString()
            .orEmpty()
            .replace(Regex("[\r\n]+"), " ")
            .replace(Regex(" {2}"), " ")
        val displayValue = if (valueString.length > dropAfter) {
            valueString.take(dropAfter) + "... (${valueString.length} symbols)"
        } else {
            valueString
        }
        logDebug("$keyString : $displayValue", tag)
    }
}