/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.repository.database

import com.romankryvolapov.localailauncher.common.models.ChatMessageModel

interface ChatDialogDatabaseRepository {

    fun saveChatMessage(chatMessage: ChatMessageModel)


}