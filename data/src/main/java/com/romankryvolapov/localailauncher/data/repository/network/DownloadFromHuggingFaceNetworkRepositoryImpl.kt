/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.repository.network

import com.romankryvolapov.localailauncher.data.network.api.HuggingFaceApi
import com.romankryvolapov.localailauncher.data.repository.network.base.BaseRepository
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.models.base.onFailure
import com.romankryvolapov.localailauncher.domain.models.base.onSuccess
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFromHuggingFaceNetworkRepository
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import okio.buffer
import okio.sink
import org.koin.core.component.inject
import java.io.File
import kotlin.getValue
import java.io.IOException

class DownloadFromHuggingFaceNetworkRepositoryImpl:
    DownloadFromHuggingFaceNetworkRepository,
    BaseRepository() {

    companion object {
        private const val TAG = "DownloadFromHuggingFaceNetworkRepositoryTag"
    }

    private val huggingFaceApi: HuggingFaceApi by inject()

    override fun downloadFromHuggingFace(
        fileUrl: String,
        huggingFaceToken: String,
    ): Flow<ResultEmittedData<File>> = flow {
        emit(ResultEmittedData.loading(model = null))
        getResult {
            huggingFaceApi.downloadFile(
                fileUrl = fileUrl,
                authHeader = "Bearer $huggingFaceToken",
            )
        }.onSuccess { model, message, responseCode ->
            logDebug("getUserDetails onSuccess", TAG)
            try {
                val file = saveResponseBodyToFile(model)
                emit(
                    ResultEmittedData.success(
                        message = message,
                        responseCode = responseCode,
                        model = file,
                    )
                )
            } catch (e: IOException) {
                emit(
                    ResultEmittedData.error(
                        model = null,
                        error = e,
                        title = "Saving Error",
                        message = "Failed to save downloaded file: ${e.message}",
                        errorType = null,
                        responseCode = responseCode,
                    )
                )
            }
        }.onFailure { error, title, message, responseCode, errorType ->
            logError("getUserDetails onFailure", message, TAG)
            emit(
                ResultEmittedData.error(
                    model = null,
                    error = error,
                    title = title,
                    message = message,
                    errorType = errorType,
                    responseCode = responseCode,
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun saveResponseBodyToFile(responseBody: ResponseBody): File {
        val targetDir = File("/path/to/save")
        if (!targetDir.exists()) targetDir.mkdirs()

        val targetFile = File(targetDir, "downloaded_file.task")
        responseBody.source().use { source ->
            targetFile.sink().buffer().use { sink ->
                sink.writeAll(source)
            }
        }
        return targetFile
    }

}