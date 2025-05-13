/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.mlcllm

import ai.mlc.mlcllm.MLCEngine
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionMessage
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionRole
import ai.mlc.mlcllm.OpenAIProtocol.StreamOptions
import com.romankryvolapov.localailauncher.domain.models.chat.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.UUID

class SendMessageMLCEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "SendMessageMLCEngineUseCaseTag"
    }

    @Volatile
    private var isGenerationAllowed = true

    fun invoke(
        message: String,
        dialogID: UUID,
        messageID: UUID,
        engine: MLCEngine,
    ): Flow<ResultEmittedData<ChatMessageModel>> = callbackFlow {
        logDebug("invoke", TAG)
        val job = launch(Dispatchers.IO) {
            trySend(ResultEmittedData.loading())
            isGenerationAllowed = true
            val messageStringBuilder = StringBuilder()
            val usageInfoText = StringBuilder()
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
            try {
                for (response in channel) {
                    response.choices.firstOrNull()?.delta?.content?.asText()?.let { textChunk ->
                        messageStringBuilder.append(textChunk)
                    }
                    if (response.usage == null) {
                        if (isGenerationAllowed) {
                            trySend(
                                ResultEmittedData.loading(
                                    model = ChatMessageModel(
                                        id = messageID,
                                        timeStamp = System.currentTimeMillis(),
                                        message = messageStringBuilder.toString(),
                                        messageData = "",
                                        dialogID = dialogID,
                                    )
                                )
                            )
                        } else {
                            trySend(
                                ResultEmittedData.success(
                                    message = null,
                                    responseCode = null,
                                    model = ChatMessageModel(
                                        id = messageID,
                                        timeStamp = System.currentTimeMillis(),
                                        message = messageStringBuilder.toString(),
                                        messageData = "",
                                        dialogID = dialogID,
                                    ),
                                )
                            )
                            return@launch
                        }
                    } else {
                        usageInfoText.append("prompt: ${response.usage!!.prompt_tokens} tok ")
                        usageInfoText.append("completion: ${response.usage!!.completion_tokens} tok ")
                        usageInfoText.append("total: ${response.usage!!.total_tokens} tok ")
                        usageInfoText.append("\n")
                        usageInfoText.append("prefill: ${response.usage!!.extra?.prefill_tokens_per_s?.toInt()} tok/s ")
                        usageInfoText.append("decode: ${response.usage!!.extra?.decode_tokens_per_s?.toInt()} tok/s ")
                        trySend(
                            ResultEmittedData.success(
                                message = null,
                                responseCode = null,
                                model = ChatMessageModel(
                                    id = messageID,
                                    timeStamp = System.currentTimeMillis(),
                                    message = messageStringBuilder.toString(),
                                    messageData = usageInfoText.toString(),
                                    dialogID = dialogID,
                                ),
                            )
                        )
                    }
                }
                logDebug("result: $messageStringBuilder", TAG)
            } catch (e: Exception) {
                logError("Exception: ${e.message}", e, TAG)
                trySend(
                    ResultEmittedData.error(
                        model = null,
                        error = null,
                        title = "MLC engine error",
                        responseCode = null,
                        message = e.message,
                        errorType = ErrorType.EXCEPTION,
                    )
                )
            } finally {
                close()
            }
        }
        awaitClose {
            isGenerationAllowed = false
            job.cancel()
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