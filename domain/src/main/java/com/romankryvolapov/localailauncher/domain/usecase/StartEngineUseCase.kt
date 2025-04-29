/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase

import ai.mlc.mlcllm.MLCEngine
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.component.inject
import java.io.File
import kotlin.getValue

class StartEngineUseCase : BaseUseCase {

    companion object {
        private const val TAG = "StartEngineUseCaseTag"
    }

    private val engine: MLCEngine by inject()

    fun invoke(
        filesDir: File,
        modelLib: String,
        modelName: String,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val modelDir = File(filesDir, modelName)
            val modelPath = modelDir.absolutePath
            engine.reset()
            engine.unload()
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
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

}