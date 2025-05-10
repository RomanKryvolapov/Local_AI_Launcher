/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

fun interface DeepCopy<T> {
    fun deepCopy(): T
}