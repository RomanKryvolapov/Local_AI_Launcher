/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.database.converters

import androidx.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun toString(list: MutableList<String>): String {
        return StringBuilder().run {
            list.forEachIndexed { index, s ->
                append(s)
                if (index + 1 < list.size) {
                    append(SEPARATOR)
                }
            }
            toString()
        }
    }

    @TypeConverter
    fun fromString(data: String): List<String> {
        return mutableListOf<String>().apply {
            if (data.isNotBlank()) {
                data.split(SEPARATOR).forEach {
                    add(it)
                }
            }
        }
    }

    companion object {
        private const val SEPARATOR = "###"
    }

}