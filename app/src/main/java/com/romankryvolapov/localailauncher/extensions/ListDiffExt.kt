/**
 * Compares two objects by their parameters. The [properties] must
 * always be the properties of the [T] class. Otherwise, this function
 * will fail due to [UNCHECKED_CAST].
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import com.romankryvolapov.localailauncher.models.common.DeepCopy
import com.romankryvolapov.localailauncher.models.common.DiffEquals

@Suppress("UNCHECKED_CAST")
inline fun <reified T> T.equalTo(other: Any?, vararg properties: T.() -> Any?): Boolean {
    if (other !is T) return false
    properties.forEach {
        val currentValue = it.invoke(this)
        val otherValue = it.invoke(other)
        if (currentValue is List<*>) {
            if (otherValue !is List<*> || !currentValue.isListContentEqualsOrSame(otherValue)) {
                return false
            }
        } else if (it.invoke(this) != it.invoke(other)) {
            return false
        }
    }
    return true
}

/**
 * Compare two lists by their content.
 * The entity of this list must have a DiffEquals implementation.
 * @return true when same, false otherwise
 */
fun <T : DiffEquals> List<T>.isListContentSame(other: List<T>): Boolean {
    if (size != other.size) return false
    withIndex().forEach {
        if (!it.value.isContentSame(other[it.index])) {
            return false
        }
    }
    return true
}

/**
 * Compare two lists by their content.
 * This method uses a default equal operation to compare list items
 * @return true when same, false otherwise
 */
fun List<*>.isListContentEquals(other: List<*>): Boolean {
    if (size != other.size) return false
    withIndex().forEach {
        if (it.value != other[it.index]) {
            return false
        }
    }
    return true
}

/**
 * Compare two lists by their content.
 * This method uses a default equal operation to compare list items
 * or the diff equals mechanic if the list item is implements [DiffEquals]
 * @return true when same, false otherwise
 */
fun List<*>.isListContentEqualsOrSame(other: List<*>): Boolean {
    if (size != other.size) return false
    withIndex().forEach {
        if (it.value is DiffEquals && other[it.index] is DiffEquals) {
            if (!(it.value as DiffEquals).isContentSame(other[it.index])) {
                return false
            }
        } else if (it.value != other[it.index]) {
            return false
        }
    }
    return true
}

/**
 * Makes a deep copy of the list. The items in the list should implement
 * [DeepCopy] interface.
 */
fun <T : DeepCopy<T>> List<T>.deepCopy(): List<T> {
    val oldList = this
    return mutableListOf<T>().apply {
        oldList.forEach {
            add(it.deepCopy())
        }
    }
}

fun <T> List<T>.moveItemToFirstPosition(predicate: (T) -> Boolean): List<T> {
    for (element in this.withIndex()) {
        if (predicate(element.value)) {
            return this.toMutableList().apply {
                removeAt(element.index)
                add(0, element.value)
            }.toList()
        }
    }
    return this
}