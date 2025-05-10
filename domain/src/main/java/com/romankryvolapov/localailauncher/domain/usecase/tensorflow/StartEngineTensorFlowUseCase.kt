/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.tensorflow

import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class StartEngineTensorFlowUseCase : BaseUseCase {

    companion object {
        private const val TAG = "StartEngineTensorFlowUseCaseTag"
    }

    fun invoke(
        modelFile: File,
    ): Flow<ResultEmittedData<Interpreter>> = flow {
        emit(ResultEmittedData.loading())
        try {
            val modelBuffer = loadModelFromAssets(modelFile)
            if (modelBuffer == null) {
                emit(
                    ResultEmittedData.error(
                        model = null,
                        error = null,
                        title = "TensorFlow file error",
                        responseCode = null,
                        message = "File not found",
                        errorType = ErrorType.EXCEPTION,
                    )
                )
            } else {
                emit(
                    ResultEmittedData.success(
                        model = Interpreter(modelBuffer),
                        message = null,
                        responseCode = null,
                    )
                )
            }
        } catch (e: Exception) {
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "TensorFlow engine error",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }

    private fun loadModelFromAssets(modelFile: File): ByteBuffer? {
        try {
            val fileInputStream = FileInputStream(modelFile)
            val fileChannel = fileInputStream.channel
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
            fileChannel.close()
            fileInputStream.close()
            return modelBuffer
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


}