package com.romankryvolapov.localailauncher.domain.models.engines

import com.romankryvolapov.localailauncher.llama.LLamaAndroid
import kotlinx.coroutines.flow.Flow

interface LLamaAndroidInterface {
    fun send(message: String, formatChat: Boolean = false): Flow<String>
    fun instance(): LLamaAndroid
    suspend fun load(pathToModel: String)
}