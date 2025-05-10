/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.models.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chatMessage")
data class ChatMessageEntity(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "dialogID") val dialogID: UUID,
    val timeStamp: Long,
    val message: String,
    val messageData: String
)

