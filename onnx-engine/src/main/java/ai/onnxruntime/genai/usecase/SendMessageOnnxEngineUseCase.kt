/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package ai.onnxruntime.genai.usecase

import ai.onnxruntime.genai.GeneratorParams
import ai.onnxruntime.genai.engine
import com.romankryvolapov.localailauncher.common.models.ChatMessageModel
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.function.Consumer

class SendMessageOnnxEngineUseCase {

    companion object {
        private const val TAG = "SendMessageOnnxEngineUseCaseTag"
    }

    @Volatile
    private var isGenerationAllowed = true

    private class GenerationCanceledException : RuntimeException("Generation cancelled")

    fun invoke(
        message: String,
        dialogID: UUID,
        messageID: UUID,
        maxLength: Double = 128.0,
        temperature: Double = 0.7,
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
            try {
                val params: GeneratorParams = engine!!.createGeneratorParams().apply {
                    setSearchOption("max_length", maxLength)
                    setSearchOption("temperature", temperature)
                }
                val listener = Consumer<String> { chunkText ->
                    logDebug("chunkText: $chunkText", TAG)
                    messageStringBuilder.append(chunkText)
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
                        throw GenerationCanceledException()
                    }
                }
                val fullResponse = engine!!.generate(params, message, listener)
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
                logDebug("result: $fullResponse", TAG)
            } catch (e: GenerationCanceledException) {
                logDebug("GenerationCanceledException: ${e.message}", TAG)
                trySend(
                    ResultEmittedData.success(
                        model = ChatMessageModel(
                            id = messageID,
                            timeStamp = System.currentTimeMillis(),
                            message = messageStringBuilder.toString(),
                            messageData = "",
                            dialogID = dialogID,
                        ),
                        message = null,
                        responseCode = null,
                    )
                )
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
            } finally {
                close()
            }
        }
        awaitClose {
            isGenerationAllowed = false
            job.cancel()
        }
    }

    fun cancel() {
        isGenerationAllowed = false
    }

}