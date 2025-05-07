/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.models.database

import androidx.room.Relation

data class ChatDialogWithMessages(
    val chatDialog: ChatDialogEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "dialogID"
    )
    val messages: List<ChatMessageEntity>,
)
