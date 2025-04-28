/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.usecase

import android.content.res.AssetManager
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class CopyAssetsToFileUseCase : BaseUseCase {

    companion object {
        private const val TAG = "GetAdministratorsUseCaseTag"
    }

    fun invoke(
        assetManager: AssetManager,
        modelName: String,
        filesDir: File
    ): Flow<Boolean> = flow {
        try {

            val filesDir = File(filesDir, modelName)
            if (!filesDir.exists()) {
                filesDir.mkdirs()
            }
            assetManager.list(modelName)?.forEach { filename ->
                val inStream = assetManager.open("$modelName/$filename")
                val outFile = File(filesDir, filename)
                val outStream = outFile.outputStream()
                inStream.copyTo(outStream)
                inStream.close()
                outStream.close()
            }
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
}