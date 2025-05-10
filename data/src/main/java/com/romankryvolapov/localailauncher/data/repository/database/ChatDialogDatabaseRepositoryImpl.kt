/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.repository.database

import com.romankryvolapov.localailauncher.data.database.dao.ChatMessageDao
import com.romankryvolapov.localailauncher.data.mappers.database.ChatMessageEntityMapper
import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.repository.database.ChatDialogDatabaseRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatDialogDatabaseRepositoryImpl :
    ChatDialogDatabaseRepository,
    KoinComponent {

    companion object {
        private const val TAG = "ChatDialogDatabaseRepositoryTag"
    }

    private val chatMessageDao: ChatMessageDao by inject()
    private val chatMessageEntityMapper: ChatMessageEntityMapper by inject()

    override fun saveChatMessage(chatMessage: ChatMessageModel) {
        val chatMessageEntity = chatMessageEntityMapper.map(chatMessage)
        chatMessageDao.saveChatMessage(chatMessageEntity)
    }

}