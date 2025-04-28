/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.romankryvolapov.localailauncher.data.database.converters.BigDecimalToStringConverter
import com.romankryvolapov.localailauncher.data.database.converters.DateToLongConverter
import com.romankryvolapov.localailauncher.data.database.converters.StringListConverter

//@Database(
//    entities = [
//
//    ],
//    version = 1,
//    exportSchema = false
//)
//
//@TypeConverters(
//    value = [
//        BigDecimalToStringConverter::class,
//        DateToLongConverter::class,
//        StringListConverter::class,
//    ]
//)
//
//abstract class AppDatabase : RoomDatabase()