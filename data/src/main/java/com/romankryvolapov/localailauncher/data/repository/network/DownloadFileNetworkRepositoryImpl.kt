/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.repository.network

import android.app.DownloadManager
import android.content.Context
import androidx.core.net.toUri
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.data.BuildConfig
import com.romankryvolapov.localailauncher.data.di.DOWNLOAD_CLIENT
import com.romankryvolapov.localailauncher.data.repository.network.base.BaseRepository
import com.romankryvolapov.localailauncher.domain.Model
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFileNetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.component.inject
import java.io.File

class DownloadFileNetworkRepositoryImpl :
    DownloadFileNetworkRepository,
    BaseRepository() {

    companion object {
        private const val TAG = "DownloadFileNetworkRepositoryImplTag"
    }

    private val downloadManager: DownloadManager by inject()
    private val okHttpClient: OkHttpClient by inject(DOWNLOAD_CLIENT)

    override fun downloadWithManager(
        model: Model,
        context: Context,
    ): Flow<ResultEmittedData<File>> = callbackFlow {
        logDebug("downloadWithManager", TAG)
        var pollingJob: Job? = null
        try {
            val destinationUri = model.file.toUri()
            val request = DownloadManager.Request(model.fileUrl?.toUri()).apply {
                setTitle("Download AI model files")
                setDescription("Download AI model files in progress")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationUri(destinationUri)
                if (model.isNeedAuthorization) {
                    if (model.isNeedAuthorization) {
                        val token = BuildConfig.AUTH_TOKEN
                        addRequestHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                    }
                }
            }
            val downloadId = downloadManager.enqueue(request)
            pollingJob = launch(Dispatchers.IO) {
                var isActiveDownload = true
                while (isActiveDownload && isActive) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val status =
                                it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                            when (status) {
                                DownloadManager.STATUS_PENDING -> {
                                    logDebug("status STATUS_PENDING", TAG)
                                    trySend(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = "Waiting for download to start"
                                        )
                                    )
                                }

                                DownloadManager.STATUS_RUNNING -> {
                                    logDebug("status STATUS_RUNNING", TAG)
                                    val downloaded = it.getLong(
                                        it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                                    )
                                    val total = it.getLong(
                                        it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                    )
                                    val progress =
                                        if (total > 0) (downloaded * 100 / total).toInt() else 0
                                    trySend(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = "Download progress: $progress%"
                                        )
                                    )
                                }

                                DownloadManager.STATUS_PAUSED -> {
                                    logError("status STATUS_RUNNING", TAG)
                                    val reason =
                                        it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                                    trySend(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = "Download pause: ${getErrorReason(reason)}"
                                        )
                                    )
                                }

                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    logDebug("status STATUS_SUCCESSFUL", TAG)
                                    val uriString = it.getString(
                                        it.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                                    )
                                    val downloadedFile = File(uriString.toUri().path!!)
                                    trySend(
                                        ResultEmittedData.success(
                                            model = downloadedFile,
                                            responseCode = null,
                                            message = "Download completed"
                                        )
                                    )
                                    isActiveDownload = false
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    logError("status STATUS_FAILED", TAG)
                                    val reason =
                                        it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                                    trySend(
                                        ResultEmittedData.error(
                                            model = null,
                                            error = null,
                                            title = "Download error",
                                            message = "Reason: ${getErrorReason(reason)}",
                                            errorType = null,
                                            responseCode = null
                                        )
                                    )
                                    isActiveDownload = false
                                }

                                else -> {
                                    logError("Unknown status: $status", TAG)
                                }
                            }
                        }
                    }
                    delay(500)
                }
            }
        } catch (e: Exception) {
            logError("Download exception", e, TAG)
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    responseCode = null,
                    message = e.message,
                    title = "Download error",
                    errorType = ErrorType.EXCEPTION,
                )
            )
        }
        awaitClose {
            pollingJob?.cancel()
        }
    }.flowOn(Dispatchers.IO)

    override fun downloadWithManager(
        context: Context,
        models: List<Model>,
    ): Flow<ResultEmittedData<List<File>>> = callbackFlow {
        logDebug("downloadWithManager", TAG)
        try {
            val pendingDownloads = mutableListOf<Pair<Long, File>>()
            models.forEach { model ->
                val destinationUri = model.file.toUri()
                val request = DownloadManager.Request(model.fileUrl?.toUri()).apply {
                    setTitle("Download AI model file ${model.file.name}")
                    setDescription("Downloading ${model.file.name}")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationUri(destinationUri)
                    if (model.isNeedAuthorization) {
                        val token = BuildConfig.AUTH_TOKEN
                        addRequestHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                    }
                }
                val downloadId = downloadManager.enqueue(request)
                pendingDownloads += downloadId to model.file
            }
            val downloadedFiles = mutableListOf<File>()
            val activeDownloads = pendingDownloads.toMutableList()
            logDebug("models size: ${models.size}", TAG)
            while (activeDownloads.isNotEmpty() && isActive) {
                for ((downloadId, file) in activeDownloads.toList()) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor == null) {
                        logError("cursor == null", TAG)
                        close()
                        return@callbackFlow
                    }
                    if (!cursor.moveToFirst()) {
                        logError("cursor not moveToFirst", TAG)
                        close()
                        return@callbackFlow
                    }
                    val status =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_PENDING -> {
                            logDebug("status STATUS_PENDING", TAG)
                            trySend(
                                ResultEmittedData.loading(
                                    model = downloadedFiles,
                                    message = "Waiting to start ${file.name}"
                                )
                            )
                        }

                        DownloadManager.STATUS_RUNNING -> {
                            logDebug("status STATUS_RUNNING", TAG)
                            val downloadedBytes = cursor.getLong(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                            )
                            val totalBytes = cursor.getLong(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                            )
                            val progress =
                                if (totalBytes > 0) (downloadedBytes * 100 / totalBytes).toInt() else 0
                            trySend(
                                ResultEmittedData.loading(
                                    model = downloadedFiles,
                                    message = "Downloading ${file.name}: $progress%"
                                )
                            )
                        }

                        DownloadManager.STATUS_PAUSED -> {
                            logError("status STATUS_PAUSED", TAG)
                            val reason = cursor.getInt(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                            )
                            trySend(
                                ResultEmittedData.loading(
                                    model = downloadedFiles,
                                    message = "Paused ${file.name}: ${getErrorReason(reason)}"
                                )
                            )
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            logDebug("status STATUS_SUCCESSFUL", TAG)
                            val uriString = cursor.getString(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                            )
                            val savedFile = File(uriString.toUri().path!!)
                            downloadedFiles.add(savedFile)
                            trySend(
                                ResultEmittedData.loading(
                                    model = downloadedFiles,
                                    message = "Saved ${downloadedFiles.size}/${models.size}"
                                )
                            )
                            activeDownloads.remove(downloadId to file)
                        }

                        DownloadManager.STATUS_FAILED -> {
                            val reason = cursor.getInt(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                            )
                            logError("status STATUS_FAILED, reason: $reason", TAG)
                            trySend(
                                ResultEmittedData.error(
                                    model = downloadedFiles,
                                    error = null,
                                    title = "Download error",
                                    message = "${file.name} failed: ${getErrorReason(reason)}",
                                    errorType = null,
                                    responseCode = null
                                )
                            )
                            close()
                            return@callbackFlow
                        }
                    }
                }
                delay(500)
            }
            if (downloadedFiles.size == models.size) {
                trySend(
                    ResultEmittedData.success(
                        model = downloadedFiles,
                        responseCode = null,
                        message = "All files downloaded successfully"
                    )
                )
            }
        } catch (e: Exception) {
            logError("downloadMultiple exception", e, "DownloadMultipleTag")
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "Download error",
                    message = e.message,
                    errorType = ErrorType.EXCEPTION,
                    responseCode = null
                )
            )
        } finally {
            close()
        }
    }.flowOn(Dispatchers.IO)

    private fun getErrorReason(reason: Int): String = when (reason) {
        DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume"
        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Device not found"
        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
        DownloadManager.ERROR_FILE_ERROR -> "File error"
        DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP data error"
        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Not enough space"
        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Unexpected HTTP code"
        DownloadManager.ERROR_UNKNOWN -> "Unknown error"
        DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "Waiting for Wi-Fi"
        DownloadManager.PAUSED_UNKNOWN -> "Paused"
        DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "Waiting for network"
        DownloadManager.PAUSED_WAITING_TO_RETRY -> "Waiting to retry"
        else -> "Code $reason"
    }

    override fun downloadDirectly(
        context: Context,
        models: List<Model>,
    ): Flow<ResultEmittedData<List<File>>> = flow {
        logDebug("downloadDirectly size: ${models.size}", TAG)
        val downloadedFiles = mutableListOf<File>()
        models.forEachIndexed { index, model ->
            try {
                val file = if (model.file.isDirectory) {
                    if (!model.file.exists()) {
                        logDebug("file isDirectory and mkdirs", TAG)
                        model.file.mkdirs()
                    }
                    val fileName = model.fileUrl!!.toUri().lastPathSegment
                    File(model.file, fileName!!)
                } else {
                    val parentDir = model.file.parentFile
                    if (parentDir != null && !parentDir.exists()) {
                        logDebug("file isFIle and mkdirs", TAG)
                        parentDir.mkdirs()
                    }
                    model.file
                }
                emit(
                    ResultEmittedData.loading(
                        model = null,
                        message = "Download started for ${model.engineName} / ${model.modelName} : (${index + 1}/${models.size})"
                    )
                )
                val requestBuilder = Request.Builder().url(model.fileUrl!!)
                if (model.isNeedAuthorization) {
                    val token = BuildConfig.AUTH_TOKEN
                    logDebug("token: $token", TAG)
                    requestBuilder.addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                }
                okHttpClient.newCall(requestBuilder.build()).execute().use { response ->
                    if (!response.isSuccessful) {
                        logError("response is not isSuccessful", TAG)
                        emit(
                            ResultEmittedData.error(
                                model = null,
                                error = null,
                                title = "Download error",
                                message = "Code ${response.code} for ${model.engineName} / ${model.modelName}",
                                responseCode = response.code,
                                errorType = ErrorType.SERVER_ERROR
                            )
                        )
                        return@forEachIndexed
                    }
                    if (file.exists()) {
                        val localSize = file.length()
                        val remoteSize = response.header("Content-Length")?.toLongOrNull()
                        logDebug("file exists, localSize: $localSize remoteSize: $remoteSize", TAG)
                        if (remoteSize != null && remoteSize == localSize) {
                            emit(
                                ResultEmittedData.loading(
                                    model = null,
                                    message = "Skipped ${model.engineName}/${model.modelName}  (${localSize} bytes)"
                                )
                            )
                            downloadedFiles += file
                            return@forEachIndexed
                        }
                    }
                    file.delete()
                    val body = response.body
                    if (body == null) {
                        logError("body == null", TAG)
                        emit(
                            ResultEmittedData.error(
                                model = null,
                                error = null,
                                title = "Empty response body",
                                message = "Empty response body for ${model.engineName} / ${model.modelName}",
                                responseCode = null,
                                errorType = ErrorType.EXCEPTION
                            )
                        )
                        return@forEachIndexed
                    }
                    var lastEmitTime = 0L
                    body.byteStream().use { input ->
                        file.outputStream().use { output ->
                            val totalBytes = body.contentLength()
                            var downloadedBytes = 0L
                            val buffer = ByteArray(8 * 1024)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                downloadedBytes += read
                                val now = System.currentTimeMillis()
                                if (now - lastEmitTime >= 1_000L) {
                                    lastEmitTime = now
                                    logDebug(
                                        "totalBytes $totalBytes downloadedBytes: $downloadedBytes",
                                        TAG
                                    )
                                    emit(
                                        ResultEmittedData.loading(
                                            model = downloadedFiles,
                                            message = "totalBytes $totalBytes downloadedBytes: $downloadedBytes",
                                        )
                                    )
                                }
                                if (totalBytes > 0) {
                                    val progress = (downloadedBytes * 100 / totalBytes).toInt()
                                    emit(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = "Download for ${model.engineName} / ${model.modelName} : $progress%"
                                        )
                                    )
                                }
                            }
                        }
                    }
                    downloadedFiles += file
                    emit(
                        ResultEmittedData.loading(
                            model = null,
                            message = "Saved ${downloadedFiles.size}/${models.size}"
                        )
                    )
                }
            } catch (e: Exception) {
                logError("direct download error", e, TAG)
                emit(
                    ResultEmittedData.error(
                        model = null,
                        error = e,
                        title = "Download error for ${model.engineName} / ${model.modelName}",
                        message = e.message,
                        responseCode = null,
                        errorType = ErrorType.EXCEPTION
                    )
                )
                return@flow
            }
        }
        emit(
            ResultEmittedData.success(
                model = downloadedFiles,
                message = "All files downloaded successfully",
                responseCode = null
            )
        )
    }.flowOn(Dispatchers.IO).conflate()

//    override fun downloadMultipleToExternalFilesDirectory(
//        filesMap: Map<String, File>,
//        context: Context,
//        huggingFaceToken: String? = null
//    ): Flow<ResultEmittedData<List<File>>> = callbackFlow {
//        logDebug("downloadMultipleToExternalFilesDirectory", TAG)
//        val downloadedFiles = mutableListOf<File>()
//        try {
//            filesMap.entries.forEachIndexed { index, (url, file) ->
//                trySend(
//                    ResultEmittedData.loading(
//                        model   = downloadedFiles,
//                        message = "Starting download of ${file.name} (${index + 1}/${filesMap.size})"
//                    )
//                )
//                downloadToExternalFilesDirectory(file, url, context, huggingFaceToken)
//                    .collect { result ->
//                        result
//                            .onLoading { _, progress ->
//                                trySend(
//                                    ResultEmittedData.loading(
//                                        model   = downloadedFiles,
//                                        message = "Downloading ${file.name}: $progress%"
//                                    )
//                                )
//                            }
//                            .onSuccess { savedFile, _, _ ->
//                                downloadedFiles.add(savedFile)
//                                trySend(
//                                    ResultEmittedData.loading(
//                                        model   = downloadedFiles,
//                                        message = "Saved ${downloadedFiles.size}/${filesMap.size}"
//                                    )
//                                )
//                            }
//                            .onFailure { _, title, message, _, _ ->
//                                logError("Error downloading ${file.name}: $message", TAG)
//                                trySend(
//                                    ResultEmittedData.error(
//                                        model        = downloadedFiles,
//                                        error        = null,
//                                        title        = title,
//                                        message      = message,
//                                        responseCode = null,
//                                        errorType    = null
//                                    )
//                                )
//                            }
//                    }
//            }
//            if (downloadedFiles.size == filesMap.size) {
//                trySend(
//                    ResultEmittedData.success(
//                        model        = downloadedFiles,
//                        message      = "All files downloaded successfully",
//                        responseCode = null
//                    )
//                )
//            } else {
//                trySend(
//                    ResultEmittedData.error(
//                        model        = downloadedFiles,
//                        error        = null,
//                        title        = "Partial failure",
//                        message      = "Successfully downloaded ${downloadedFiles.size}, failed ${filesMap.size - downloadedFiles.size}",
//                        responseCode = null,
//                        errorType    = null
//                    )
//                )
//            }
//        } catch (e: Exception) {
//            logError("downloadMultipleToExternalFilesDirectory error", e, TAG)
//            trySend(
//                ResultEmittedData.error(
//                    model        = null,
//                    error        = e,
//                    title        = "Engine error",
//                    message      = e.message,
//                    responseCode = null,
//                    errorType    = ErrorType.EXCEPTION
//                )
//            )
//        } finally {
//            close()
//        }
//    }.flowOn(Dispatchers.IO)

//    private fun saveResponseBodyToFile(responseBody: ResponseBody): File {
//        val targetDir = File("/path/to/save")
//        if (!targetDir.exists()) targetDir.mkdirs()
//
//        val targetFile = File(targetDir, "downloaded_file.task")
//        responseBody.source().use { source ->
//            targetFile.sink().buffer().use { sink ->
//                sink.writeAll(source)
//            }
//        }
//        return targetFile
//    }

}