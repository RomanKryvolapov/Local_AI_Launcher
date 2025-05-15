/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.usecase

import android.content.res.AssetManager
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import  com.romankryvolapov.localailauncher.common.models.common.BaseUseCase
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class CopyAssetFileToUserFilesUseCase : BaseUseCase {

    companion object {
        private const val TAG = "CopyAssetFileToUserFilesUseCaseTag"
    }

    fun invoke(
        filesDir: File,
        modelName: String,
        assetManager: AssetManager,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            clearFilesDir(filesDir)
            val modelDir = File(filesDir, modelName)
            if (!modelDir.exists()) {
                modelDir.mkdirs()
            }
            assetManager.list(modelName)?.forEach { filename ->
                assetManager.open("$modelName/$filename").use { inStream ->
                    File(modelDir, filename).outputStream().use { outStream ->
                        inStream.copyTo(outStream)
                    }
                }
            }
            emit(
                ResultEmittedData.success(
                    model = Unit,
                    message = null,
                    responseCode = null,
                )
            )
        } catch (e: Exception) {
            logError("Error copying assets", e, TAG)
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    responseCode = null,
                    message = e.message,
                    title = "Engine error",
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun clearFilesDir(filesDir: File) {
        filesDir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
        }
    }

}