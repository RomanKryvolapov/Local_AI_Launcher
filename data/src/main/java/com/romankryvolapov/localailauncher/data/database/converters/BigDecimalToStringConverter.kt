/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.database.converters

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalToStringConverter {

    @TypeConverter
    fun toString(data: BigDecimal?): String? {
        if (data == null) return null
        return data.toPlainString()
    }

    @TypeConverter
    fun fromString(data: String?): BigDecimal? {
        if (data == null) return null
        return BigDecimal(data)
    }

}