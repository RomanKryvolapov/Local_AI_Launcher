package com.romankryvolapov.localailauncher.domain.usecase.llama

import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.models.chat.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.llama.LLamaAndroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.util.UUID

class SendMessageLLamaCppEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "SendMessageLLamaCppEngineUseCaseTag"
    }

    @Volatile
    private var isGenerationAllowed = true

    fun invoke(
        dialogID: UUID,
        messageID: UUID,
        message: String,
        engine: LLamaAndroid,
    ): Flow<ResultEmittedData<ChatMessageModel>> = callbackFlow {
        logDebug("invoke", TAG)
        val job = launch(Dispatchers.IO) {
            trySend(ResultEmittedData.loading())
            isGenerationAllowed = true
            val messageStringBuilder = StringBuilder()
            engine.send(message).onCompletion { error ->
                if (error == null) {
                    logDebug("onCompletion ", TAG)
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
            isGenerationAllowed = false
            job.cancel()
        }
    }.flowOn(Dispatchers.IO)

    fun cancel() {
        isGenerationAllowed = false
    }

}