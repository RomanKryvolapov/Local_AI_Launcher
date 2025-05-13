package com.romankryvolapov.localailauncher.domain.usecase.onnx

import ai.onnxruntime.genai.GeneratorParams
import ai.onnxruntime.genai.SimpleGenAI
import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import java.util.function.Consumer

class SendMessageOnnxEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "SendMessageOnnxEngineUseCaseTag"
    }

    fun invoke(
        message: String,
        dialogID: UUID,
        messageID: UUID,
        maxLength: Double = 128.0,
        temperature: Double = 0.7,
        engine: SimpleGenAI,
    ): Flow<ResultEmittedData<ChatMessageModel>> = callbackFlow {
        logDebug("invoke", TAG)
        try {
            val params: GeneratorParams = engine.createGeneratorParams().apply {
                setSearchOption("max_length", maxLength)
                setSearchOption("temperature", temperature)
            }
            val stringBuilder = StringBuilder()
            val listener = Consumer<String> { chunkText ->
                logDebug("ChunkText: $chunkText", TAG)
                stringBuilder.append(chunkText)
                trySend(
                    ResultEmittedData.loading(
                        model = ChatMessageModel(
                            id = messageID,
                            timeStamp = System.currentTimeMillis(),
                            message = stringBuilder.toString(),
                            messageData = "",
                            dialogID = dialogID,
                        )
                    )
                )
            }
            val fullResponse = engine.generate(params, message, listener)
            logDebug("Result: $fullResponse", TAG)
            trySend(
                ResultEmittedData.success(
                    model = ChatMessageModel(
                        id = messageID,
                        timeStamp = System.currentTimeMillis(),
                        message = fullResponse,
                        messageData = "",
                        dialogID = dialogID,
                    ),
                    message = null,
                    responseCode = null,
                )
            )
            close()
        } catch (e: Exception) {
            logError("Exception: ${e.message}", e, TAG)
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    responseCode = null,
                    message = e.message,
                    title = "ONNX engine error",
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }
}