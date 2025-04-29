package com.romankryvolapov.localailauncher.domain.usecase

import ai.mlc.mlcllm.MLCEngine
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionMessage
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionRole
import ai.mlc.mlcllm.OpenAIProtocol.StreamOptions
import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.inject
import java.util.UUID

class SendMessageUseCase : BaseUseCase {

    companion object {
        private const val TAG = "CopyAssetsToFileUseCaseTag"
    }

    private val engine: MLCEngine by inject()

    @Volatile
    private var isGenerationAllowed = true

    fun invoke(
        message: String,
        messageID: UUID,
    ): Flow<ResultEmittedData<ChatMessageModel>> = flow {
        isGenerationAllowed = true
        try {
            val streamingResponse = StringBuilder()
            var usageInfoText = StringBuilder()
            val channel = engine.chat.completions.create(
                messages = listOf(
                    ChatCompletionMessage(
                        role = ChatCompletionRole.user,
                        content = buildGemmaPrompt(message)
                    )
                ),
                stream_options = StreamOptions(include_usage = true),
                stop = listOf("<end_of_turn>")
            )
            for (response in channel) {
                response.choices.firstOrNull()?.delta?.content?.asText()?.let { textChunk ->
                    streamingResponse.append(textChunk)
                }
                response.usage?.let { usage ->
                    if (usageInfoText.isEmpty()) {
                        usageInfoText.append("prompt: ${usage.prompt_tokens} tok ")
                        usageInfoText.append("completion: ${usage.completion_tokens} tok ")
                        usageInfoText.append("total: ${usage.total_tokens} tok ")
                        usageInfoText.append("\n")
                        usageInfoText.append("prefill: ${usage.extra?.prefill_tokens_per_s?.toInt()} tok/s ")
                        usageInfoText.append("decode: ${usage.extra?.decode_tokens_per_s?.toInt()} tok/s ")
                    }
                }
                emit(
                    ResultEmittedData.loading(
                        model = ChatMessageModel(
                            id = messageID,
                            timeStamp = System.currentTimeMillis(),
                            message = streamingResponse.toString(),
                            messageData = "",
                        )
                    )
                )
                if (!isGenerationAllowed) {
                    logDebug("Manual cancellation triggered", TAG)
                    isGenerationAllowed = true
                    break
                }
            }
            emit(
                ResultEmittedData.success(
                    message = null,
                    responseCode = null,
                    model = ChatMessageModel(
                        id = messageID,
                        timeStamp = System.currentTimeMillis(),
                        message = streamingResponse.toString(),
                        messageData = usageInfoText.toString()
                    ),
                )
            )
        } catch (e: Exception) {
            logError("Error", e, TAG)
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "Engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.SERVER_DATA_ERROR,
                )
            )
        }
    }

    fun cancel() {
        isGenerationAllowed = false
    }

    private fun buildGemmaPrompt(userMessage: String): String {
        return """
        <bos><start_of_turn>user
        $userMessage
        <end_of_turn>
        <start_of_turn>model
    """.trimIndent()
    }

}