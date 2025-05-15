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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class CopyAllAssetFilesToUserFilesUseCase : BaseUseCase {

    companion object {
        private const val TAG = "CopyAllAssetFilesToUserFilesUseCaseTag"
    }

    fun invoke(
        filesDir: File,
        assetManager: AssetManager,
    ): Flow<ResultEmittedData<String>> = callbackFlow {
        logDebug("invoke", TAG)
        try {
            val totalFiles = countAssetFiles(assetManager, "")
            var copiedCount = 0
            clearFilesDir(filesDir)
            trySend(
                ResultEmittedData.loading(
                    model = "0 from $totalFiles"
                )
            )
            copyAssetsRecursively(
                assetManager,
                "",
                filesDir
            ) { filePath ->
                logDebug(filePath, TAG)
                copiedCount++
                trySend(
                    ResultEmittedData.loading(
                        model = "$copiedCount from $totalFiles:\n$filePath"
                    )
                )
            }
            trySend(
                ResultEmittedData.success(
                    model = "Coped all files",
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

    private fun countAssetFiles(assetManager: AssetManager, path: String): Int {
        val files = assetManager.list(path) ?: return 0
        if (files.isEmpty()) {
            return 1
        }
        return files.sumOf { file ->
            val subPath = if (path.isEmpty()) file else "$path/$file"
            countAssetFiles(assetManager, subPath)
        }
    }

    private fun copyAssetsRecursively(
        assetManager: AssetManager,
        path: String,
        destDir: File,
        onFileCopied: (filePath: String) -> Unit
    ) {
        val entries = assetManager.list(path) ?: return
        if (entries.isEmpty()) {
            val outFile = File(destDir, path)
            outFile.parentFile?.mkdirs()
            assetManager.open(path).use { inStream ->
                outFile.outputStream().use { outStream ->
                    inStream.copyTo(outStream)
                }
            }
            onFileCopied(path)
        } else {
            for (entry in entries) {
                val subPath = if (path.isEmpty()) entry else "$path/$entry"
                copyAssetsRecursively(assetManager, subPath, destDir, onFileCopied)
            }
        }
    }

    private fun clearFilesDir(filesDir: File) {
        filesDir.listFiles()?.forEach { file ->
            if (file.isDirectory) file.deleteRecursively()
            else file.delete()
        }
    }
}