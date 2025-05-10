/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.usecase

import android.content.res.AssetManager
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

class CopyAllAssetFilesToUserFilesUseCase : BaseUseCase {

    companion object {
        private const val TAG = "CopyAllAssetFilesToUserFilesUseCaseTag"
    }

    fun invoke(
        filesDir: File,
        assetManager: AssetManager,
    ): Flow<ResultEmittedData<Unit>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            clearFilesDir(filesDir)
            copyAssetsRecursively(assetManager, "", filesDir)
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

    private fun copyAssetsRecursively(assetManager: AssetManager, path: String, destDir: File) {
        val files = assetManager.list(path) ?: return
        if (files.isEmpty()) {
            val outFile = File(destDir, path)
            outFile.parentFile?.mkdirs()
            assetManager.open(path).use { inStream ->
                outFile.outputStream().use { outStream ->
                    inStream.copyTo(outStream)
                }
            }
        } else {
            for (file in files) {
                val subPath = if (path.isEmpty()) file else "$path/$file"
                copyAssetsRecursively(assetManager, subPath, destDir)
            }
        }
    }

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