/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.mediapipe

import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SendMessageMediaPipeUseCase : BaseUseCase {

    companion object {
        private const val TAG = "SendMessageMediaPipeUseCaseTag"
    }

    fun invoke(
        inputPrompt: String,
        llmInference: LlmInference,
    ): Flow<ResultEmittedData<String>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val result = llmInference.generateResponse(inputPrompt)
            if (result.isEmpty()) {
                emit(
                    ResultEmittedData.error(
                        model = null,
                        error = null,
                        title = "Engine error",
                        responseCode = null,
                        message = "Result is empty",
                        errorType = ErrorType.EXCEPTION,
                    )
                )
            } else {
                emit(
                    ResultEmittedData.success(
                        model = result,
                        responseCode = null,
                        message = null,
                    )
                )
            }
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
    }

}