/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package ai.onnxruntime.genai.usecase

import ai.onnxruntime.genai.SimpleGenAI
import ai.onnxruntime.genai.engine
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class StartOnnxEngineUseCase {

    companion object {
        private const val TAG = "StartOnnxEngineUseCaseTag"
    }

    fun invoke(
        modelFile: File,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            engine = SimpleGenAI(modelFile.absolutePath)
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