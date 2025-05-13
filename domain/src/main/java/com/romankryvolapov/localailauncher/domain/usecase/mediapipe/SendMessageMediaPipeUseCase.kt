/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.mediapipe

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class SendMessageMediaPipeUseCase : BaseUseCase {

    companion object {
        private const val TAG = "SendMessageMediaPipeUseCaseTag"
    }

    private var future: ListenableFuture<String>? = null

    fun invoke(
        dialogID: UUID,
        messageID: UUID,
        message: String,
        llmInference: LlmInference,
        topK: Int = 40,
        topP: Float = 1.0f,
        randomSeed: Int = 0,
        temperature: Float = 0.8f,
    ): Flow<ResultEmittedData<ChatMessageModel>> = callbackFlow {
        logDebug("invoke", TAG)
        trySend(ResultEmittedData.loading())
        var session: LlmInferenceSession? = null
        try {
            val sessionOptions = LlmInferenceSessionOptions.builder()
                .setTopK(topK)
                .setTopP(topP)
                .setTemperature(temperature)
                .setRandomSeed(randomSeed)
                .build()
            session = try {
                LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
            } catch (e: Exception) {
                logError("createSession failed", e, TAG)
                llmInference.close()
                trySend(
                    ResultEmittedData.error(
                        model = null,
                        error = null,
                        title = "Engine error",
                        responseCode = null,
                        message = "Result is empty",
                        errorType = ErrorType.EXCEPTION,
                    )
                )
                llmInference.close()
                close()
                return@callbackFlow
            }
            session.addQueryChunk(message)
            val streamingResponse = StringBuilder()
            future = session.generateResponseAsync(
                ProgressListener { partial, done ->
                    logDebug("partial: $partial done: #done", TAG)
                    if (!done) {
                        streamingResponse.append(partial)
                        val loading = ChatMessageModel(
                            id = messageID,
                            message = streamingResponse.toString(),
                            dialogID = dialogID,
                            messageData = "",
                            timeStamp = System.currentTimeMillis(),
                        )
                        trySend(
                            ResultEmittedData.loading(
                                model = loading
                            )
                        )
                    }
                }
            )
            future?.addListener({
                val fullText = future?.get()
                logDebug("fullText: $fullText", TAG)
                if (fullText?.isEmpty() == true) {
                    trySend(
                        ResultEmittedData.error(
                            model = null,
                            error = null,
                            title = "Engine error",
                            message = "Empty response",
                            responseCode = null,
                            errorType = ErrorType.EXCEPTION
                        )
                    )
                } else {
                    trySend(
                        ResultEmittedData.success(
                            model = ChatMessageModel(
                                id = messageID,
                                messageData = "",
                                message = fullText ?: "",
                                dialogID = dialogID,
                                timeStamp = System.currentTimeMillis(),
                            ),
                            message = null,
                            responseCode = null,
                        )
                    )
                }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) {
            logError("Exception: ${e.message}", e, TAG)
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "MediaPipe engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
        awaitClose {
            session?.close()
            llmInference.close()
        }
    }

    fun cancel() {
        future?.cancel(true)
    }

}