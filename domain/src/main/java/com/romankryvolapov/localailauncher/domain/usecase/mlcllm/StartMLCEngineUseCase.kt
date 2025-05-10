/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.mlcllm

import ai.mlc.mlcllm.MLCEngine
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StartMLCEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "StartEngineUseCaseTag"
    }

    fun invoke(
        modelFile: File,
        modelLib: String,
    ): Flow<ResultEmittedData<MLCEngine>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val engine = MLCEngine()
            engine.reset()
            engine.unload()
            engine.reload(modelFile.absolutePath, modelLib)
            emit(
                ResultEmittedData.success(
                    model = engine,
                    message = null,
                    responseCode = null,
                )
            )
        } catch (e: Exception) {
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