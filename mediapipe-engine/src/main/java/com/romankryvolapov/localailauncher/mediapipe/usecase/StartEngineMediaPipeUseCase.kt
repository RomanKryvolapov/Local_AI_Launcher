/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.mediapipe.usecase

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.mediapipe.engine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StartEngineMediaPipeUseCase {

    companion object {
        private const val TAG = "StartEngineMediaPipeUseCaseTag"
    }

    fun invoke(
        modelFile: File,
        context: Context,
        maxTopK: Int = 64,
        maxTokens: Int = 1000,
        backend: LlmInference.Backend = LlmInference.Backend.CPU,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val interfaceOptions = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(maxTokens)
                .setMaxTopK(maxTopK)
                .setPreferredBackend(backend)
                .build()
            engine = LlmInference.createFromOptions(context, interfaceOptions)
            if (engine != null) {
                emit(
                    ResultEmittedData.success(
                        model = Unit,
                        message = null,
                        responseCode = null,
                    )
                )
                logDebug("Ready", TAG)
            } else {
                emit(
                    ResultEmittedData.error(
                        model = null,
                        error = null,
                        title = "MediaPipe engine error",
                        responseCode = null,
                        message = "MediaPipe engine is null",
                        errorType = ErrorType.EXCEPTION,
                    )
                )
                logError("llmInference == null", TAG)
            }
            logDebug("ready", TAG)
        } catch (e: Exception) {
            logError("Exception: ${e.message}", e, TAG)
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = e,
                    title = "MediaPipe engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

}