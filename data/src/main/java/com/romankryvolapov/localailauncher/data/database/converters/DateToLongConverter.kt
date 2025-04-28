/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.database.converters

import androidx.room.TypeConverter
import java.util.Date

class DateToLongConverter {

    @TypeConverter
    fun toLong(date: Date?): Long? {
        if (date == null) return null
        return date.time
    }

    @TypeConverter
    fun fromLong(date: Long?): Date? {
        if (date == null) return null
        return Date(date)
    }

}