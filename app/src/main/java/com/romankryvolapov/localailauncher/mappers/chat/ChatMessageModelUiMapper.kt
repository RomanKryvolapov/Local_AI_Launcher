/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.mappers.chat

import com.romankryvolapov.localailauncher.data.mappers.base.BaseMapper
import com.romankryvolapov.localailauncher.common.models.ChatMessageModel
import com.romankryvolapov.localailauncher.models.chat.ChatMessageModelUi

class ChatMessageModelUiMapper : BaseMapper<ChatMessageModel, ChatMessageModelUi>() {

    override fun map(from: ChatMessageModel): ChatMessageModelUi {
        return with(from) {
            ChatMessageModelUi(
                id = id,
                timeStamp = timeStamp,
                message = message,
                messageData = messageData,
            )
        }
    }

}