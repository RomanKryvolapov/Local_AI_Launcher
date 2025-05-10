/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.mediapipe

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StartEngineMediaPipeUseCase : BaseUseCase {

    companion object {
        private const val TAG = "StartEngineMediaPipeUseCaseTag"
    }

    fun invoke(
        modelFile: File,
        context: Context,
        maxTopK: Int = 64,
        maxTokens: Int = 1000,
        backend: LlmInference.Backend = LlmInference.Backend.CPU,
    ): Flow<ResultEmittedData<LlmInference>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val interfaceOptions = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(maxTokens)
                .setMaxTopK(maxTopK)
                .setPreferredBackend(backend)
                .build()
            val llmInference = LlmInference.createFromOptions(context, interfaceOptions)
            if (llmInference != null) {
                emit(
                    ResultEmittedData.success(
                        message = null,
                        responseCode = null,
                        model = llmInference,
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "exception: ${e.message}", e)
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