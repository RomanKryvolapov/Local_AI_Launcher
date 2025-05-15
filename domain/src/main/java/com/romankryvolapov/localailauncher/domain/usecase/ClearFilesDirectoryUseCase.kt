package com.romankryvolapov.localailauncher.domain.usecase

import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import  com.romankryvolapov.localailauncher.common.models.common.BaseUseCase
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class ClearFilesDirectoryUseCase : BaseUseCase {

    companion object {
        private const val TAG = "ClearFilesDirectoryUseCaseTag"
    }

    fun invoke(
        filesDir: File,
    ): Flow<ResultEmittedData<Unit>> = callbackFlow {
        logDebug("invoke", TAG)
        trySend(ResultEmittedData.loading())
        try {
            filesDir.listFiles()?.forEach { file ->
                if (file.isDirectory) file.deleteRecursively()
                else file.delete()
            }
            trySend(
                ResultEmittedData.success(
                    model = Unit,
                    message = null,
                    responseCode = null,
                )
            )
        } catch (e: Exception) {
            logError("Error copying assets", e, TAG)
            trySend(
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
        awaitClose {
        }
    }.flowOn(Dispatchers.IO)

}