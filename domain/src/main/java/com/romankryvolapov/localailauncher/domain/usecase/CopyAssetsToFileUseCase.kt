/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.usecase

import android.content.res.AssetManager
import android.util.Log
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class CopyAssetsToFileUseCase : BaseUseCase {

    companion object {
        private const val TAG = "CopyAssetsToFileUseCaseTag"
    }

    fun invoke(
        assetManager: AssetManager,
        modelName: String,
        filesDir: File
    ): Flow<Boolean> = flow {
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
            emit(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error copying assets", e)
            emit(false)
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