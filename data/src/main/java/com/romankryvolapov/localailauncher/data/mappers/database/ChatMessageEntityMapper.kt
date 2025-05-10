/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.mappers.database

import com.romankryvolapov.localailauncher.data.mappers.base.BaseReverseMapper
import com.romankryvolapov.localailauncher.data.models.database.ChatMessageEntity
import com.romankryvolapov.localailauncher.data.utils.StrictMapperConfig
import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import kotlin.jvm.java

class ChatMessageEntityMapper : BaseReverseMapper<ChatMessageModel, ChatMessageEntity>() {

    @Mapper(config = StrictMapperConfig::class)
    interface ModelMapper {
        fun map(from: ChatMessageModel): ChatMessageEntity
        fun reverse(from: ChatMessageEntity): ChatMessageModel
    }

    override fun map(from: ChatMessageModel): ChatMessageEntity {
        return Mappers.getMapper(ModelMapper::class.java).map(from)
    }

    override fun reverse(to: ChatMessageEntity): ChatMessageModel {
        return Mappers.getMapper(ModelMapper::class.java).reverse(to)
    }


}