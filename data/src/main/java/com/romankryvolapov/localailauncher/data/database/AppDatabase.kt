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
import com.romankryvolapov.localailauncher.data.database.dao.ChatDialogDao
import com.romankryvolapov.localailauncher.data.database.dao.ChatMessageDao
import com.romankryvolapov.localailauncher.data.models.database.ChatDialogEntity
import com.romankryvolapov.localailauncher.data.models.database.ChatMessageEntity

@Database(
    entities = [
        ChatDialogEntity::class,
        ChatMessageEntity::class,
    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(
    value = [
        BigDecimalToStringConverter::class,
        DateToLongConverter::class,
        StringListConverter::class,
    ]
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getChatDialogDao(): ChatDialogDao

    abstract fun getChatMessageDao(): ChatMessageDao

}