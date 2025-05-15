/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.llama.usecase

import com.romankryvolapov.localailauncher.common.models.ChatMessageModel
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.llama.engine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.util.UUID

class SendMessageLLamaCppEngineUseCase {

    companion object {
        private const val TAG = "SendMessageLLamaCppEngineUseCaseTag"
    }

    private var generationJob: Job? = null

    fun invoke(
        dialogID: UUID,
        messageID: UUID,
        message: String,
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
        trySend(ResultEmittedData.loading())
        val messageStringBuilder = StringBuilder()
        generationJob = launch(Dispatchers.IO) {
            engine!!.send(message).onCompletion { error ->
                if (error == null) {
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
                    logDebug("result: $messageStringBuilder", TAG)
                } else if (error is CancellationException) {
                    logDebug("generation cancelled", TAG)
                } else {
                    logError("onCompletion exception: ${error.message}", error, TAG)
                    trySend(
                        ResultEmittedData.error(
                            model = null,
                            error = null,
                            title = "LLama engine error",
                            responseCode = null,
                            message = error.message,
                            errorType = ErrorType.EXCEPTION,
                        )
                    )
                }
            }.catch { error ->
                logDebug("catch: $error", TAG)
                trySend(
                    ResultEmittedData.error(
                        model = null,
                        error = null,
                        title = "LLama engine error",
                        responseCode = null,
                        message = error.message,
                        errorType = ErrorType.EXCEPTION,
                    )
                )
            }.collect { text ->
                logDebug("collect: $text", TAG)
                messageStringBuilder.append(text)
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
            }
        }
        awaitClose {
            logDebug("awaitClose", TAG)
            generationJob?.cancel()
        }
    }.flowOn(Dispatchers.IO)

    fun cancel() {
        logDebug("cancel", TAG)
        generationJob?.cancel()
    }

}