/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.llama.usecase

import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.llama.LLamaAndroid
import com.romankryvolapov.localailauncher.llama.engine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StartLLamaCppEngineUseCase {

    companion object {
        private const val TAG = "StartLLamaCppEngineUseCaseTag"
    }

    fun invoke(
        modelFile: File,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            engine = LLamaAndroid()
            engine!!.load(modelFile.absolutePath)
            emit(
                ResultEmittedData.success(
                    model = Unit,
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