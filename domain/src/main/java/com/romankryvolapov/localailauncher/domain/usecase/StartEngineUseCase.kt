package com.romankryvolapov.localailauncher.domain.usecase

import ai.mlc.mlcllm.MLCEngine
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class StartEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "StartEngineUseCaseTag"
    }

    fun invoke(
        filesDir: File,
        modelLib: String,
        engine: MLCEngine,
        modelName: String,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke modelName: $modelName modelLib: $modelLib", TAG)
        try {
            val modelDir = File(filesDir, modelName)
            val modelPath = modelDir.absolutePath
            engine.reload(modelPath, modelLib)
            emit(
                ResultEmittedData.success(
                    model = Unit,
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
                    errorType = ErrorType.SERVER_DATA_ERROR,
                )
            )
        }
    }

}