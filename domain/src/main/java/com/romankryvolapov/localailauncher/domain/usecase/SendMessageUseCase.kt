package com.romankryvolapov.localailauncher.domain.usecase

import ai.mlc.mlcllm.MLCEngine
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionMessage
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionRole
import ai.mlc.mlcllm.OpenAIProtocol.ResponseFormat
import ai.mlc.mlcllm.OpenAIProtocol.StreamOptions
import android.util.Log.e
import com.romankryvolapov.localailauncher.domain.DEBUG_PRINT_PREFERENCES_INFO
import com.romankryvolapov.localailauncher.domain.Models
import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
//                model = Models.GEMMA_3_4B_QAT.modelName,
//                frequency_penalty = 0.5f, // Штраф за повторяющиеся токены
//                presence_penalty = 0.6f,  // Штраф за "новую" тему
//                logprobs = true, // Включить логарифм вероятностей
//                top_logprobs = 5, // Количество вероятностей, которые включить
//                logit_bias = mapOf(50256 to -100f), // Скрытие определенных токенов (пример)
//                max_tokens = 512, // // Максимально разрешенное количество токенов в ответе
//                n = 1, // Количество ответов
//                seed = 42, // Полезно для воспроизведения консистентных результатов.
//                stream = true, // Активирует потоковые данные или уточняет детали.
//                temperature = 0.7f, // "Творчество" модели
//                top_p = 0.9f,  // Учитывать только топ-x% вероятностей на каждом шаге
//                tools = null,
//                user = "user",
//                response_format = null,
                stream_options = StreamOptions(include_usage = true), // Активирует потоковые данные или уточняет детали.
                stop = listOf("<end_of_turn>")
            )
            for (response in channel) {
                response.choices.firstOrNull()?.delta?.content?.asText()?.let { textChunk ->
                    streamingResponse.append(textChunk)
                }
                if (response.usage == null) {
                    if (isGenerationAllowed) {
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
                    } else {
                        emit(
                            ResultEmittedData.success(
                                message = null,
                                responseCode = null,
                                model = ChatMessageModel(
                                    id = messageID,
                                    timeStamp = System.currentTimeMillis(),
                                    message = streamingResponse.toString(),
                                    messageData = "",
                                ),
                            )
                        )
                        break
                    }
                } else {
                    usageInfoText.append("prompt: ${response.usage!!.prompt_tokens} tok ")
                    usageInfoText.append("completion: ${response.usage!!.completion_tokens} tok ")
                    usageInfoText.append("total: ${response.usage!!.total_tokens} tok ")
                    usageInfoText.append("\n")
                    usageInfoText.append("prefill: ${response.usage!!.extra?.prefill_tokens_per_s?.toInt()} tok/s ")
                    usageInfoText.append("decode: ${response.usage!!.extra?.decode_tokens_per_s?.toInt()} tok/s ")
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
                }
            }
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
    }.flowOn(Dispatchers.IO)

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