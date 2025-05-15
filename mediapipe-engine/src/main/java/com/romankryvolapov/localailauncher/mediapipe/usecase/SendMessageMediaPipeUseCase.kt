/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.mediapipe.usecase

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import com.romankryvolapov.localailauncher.common.models.ChatMessageModel
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.mediapipe.engine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.UUID

class SendMessageMediaPipeUseCase {

    companion object {
        private const val TAG = "SendMessageMediaPipeUseCaseTag"
    }

    @Volatile
    private var isGenerationAllowed = true

    fun invoke(
        dialogID: UUID,
        messageID: UUID,
        message: String,
        topK: Int = 40,
        topP: Float = 1.0f,
        randomSeed: Int = 0,
        temperature: Float = 0.8f,
    ): Flow<ResultEmittedData<ChatMessageModel>> = callbackFlow {
        logDebug("invoke", TAG)
        if (engine == null) {
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "Engine error",
                    responseCode = null,
                    message = "Engine is null",
                    errorType = ErrorType.EXCEPTION,
                )
            )
            close()
            return@callbackFlow
        }
        val job = launch(Dispatchers.IO) {
            trySend(ResultEmittedData.loading())
            isGenerationAllowed = true
            val messageStringBuilder = StringBuilder()
            var future: ListenableFuture<String>? = null
            var session: LlmInferenceSession? = null
            try {
                val sessionOptions = LlmInferenceSessionOptions.builder()
                    .setTopK(topK)
                    .setTopP(topP)
                    .setTemperature(temperature)
                    .setRandomSeed(randomSeed)
                    .build()
                session = try {
                    LlmInferenceSession.createFromOptions(engine, sessionOptions)
                } catch (e: Exception) {
                    logError("createSession failed", e, TAG)
                    engine!!.close()
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
                    close()
                    return@launch
                }.apply {
                    addQueryChunk(message)
                }
                future = session.generateResponseAsync(ProgressListener { partial, done ->
                    logDebug("partial: $partial done: #done", TAG)
                    if (!done) {
                        messageStringBuilder.append(partial)
                        if (isGenerationAllowed) {
                            trySend(
                                ResultEmittedData.loading(
                                    model = ChatMessageModel(
                                        id = messageID,
                                        message = messageStringBuilder.toString(),
                                        dialogID = dialogID,
                                        messageData = "",
                                        timeStamp = System.currentTimeMillis(),
                                    )
                                )
                            )
                        } else {
                            future?.cancel(true)
                            trySend(
                                ResultEmittedData.success(
                                    model = ChatMessageModel(
                                        id = messageID,
                                        messageData = "",
                                        message = messageStringBuilder.toString(),
                                        dialogID = dialogID,
                                        timeStamp = System.currentTimeMillis(),
                                    ),
                                    message = null,
                                    responseCode = null,
                                )
                            )
                        }
                    }
                }).apply {
                    addListener({
                        val fullText = future?.get()
                        logDebug("fullText: $fullText", TAG)
                        if (fullText?.isEmpty() == true) {
                            trySend(
                                ResultEmittedData.error(
                                    model = null,
                                    error = null,
                                    title = "MediaPipe engine error",
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
                }
                logDebug("result: $messageStringBuilder", TAG)
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
        }
        awaitClose {
            isGenerationAllowed = false
            job.cancel()
        }
    }.flowOn(Dispatchers.IO)

    fun cancel() {
        isGenerationAllowed = false
    }

}