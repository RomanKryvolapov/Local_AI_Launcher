/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

fun Char.isLatin() = ('a'..'z').contains(this.lowercaseChar()) || ('A'..'Z').contains(this)

// Check if character is cyrillic excluding 'ы' and 'э'
fun Char.isCyrillic() = ('а'..'я').filter { it.code != 0x044b && it.code != 0x044d }
    .contains(this.lowercaseChar()) || ('А'..'Я').filter { it.code != 0x042b && it.code != 0x042d }.contains(this)
