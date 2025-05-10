/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.mediapipe

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
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
    ): Flow<ResultEmittedData<LlmInference>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val interfaceOptions = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.path)
                .setMaxTokens(1000)
                .setMaxTopK(64)
                .setPreferredBackend(LlmInference.Backend.CPU)
                .build()
            val llmInference = LlmInference.createFromOptions(context, interfaceOptions)
            emit(
                ResultEmittedData.success(
                    message = null,
                    responseCode = null,
                    model = llmInference,
                )
            )
        } catch (e: Exception) {
            logError("Error", e, TAG)
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "Engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

}