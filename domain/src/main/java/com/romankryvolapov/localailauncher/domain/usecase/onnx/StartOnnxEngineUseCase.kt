package com.romankryvolapov.localailauncher.domain.usecase.onnx

import ai.onnxruntime.genai.SimpleGenAI
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

class StartOnnxEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "StartOnnxEngineUseCaseTag"
    }

    fun invoke(
        modelFile: File,
    ): Flow<ResultEmittedData<SimpleGenAI>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            emit(
                ResultEmittedData.success(
                    model = SimpleGenAI(modelFile.absolutePath),
                    message = null,
                    responseCode = null,
                )
            )
            logDebug("ready", TAG)
        } catch (e: Exception) {
            logError("Exception: ${e.message}", e, TAG)
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = e,
                    title = "ONNX engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

}