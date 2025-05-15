/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.mlcllm.usecase

import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.mlcllm.MLCEngine
import com.romankryvolapov.localailauncher.mlcllm.engine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StartMLCEngineUseCase {

    companion object {
        private const val TAG = "StartMLCEngineUseCaseTag"
    }

    fun invoke(
        modelFile: File,
        modelLib: String,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            engine = MLCEngine().apply {
                reset()
                unload()
                reload(modelFile.absolutePath, modelLib)
            }
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
                    title = "MLC engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

}