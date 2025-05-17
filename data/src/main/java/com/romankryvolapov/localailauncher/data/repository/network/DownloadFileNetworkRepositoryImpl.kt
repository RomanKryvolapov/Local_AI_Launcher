/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.repository.network

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.data.repository.network.base.BaseRepository
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFileNetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.component.inject
import java.io.File

class DownloadFileNetworkRepositoryImpl :
    DownloadFileNetworkRepository,
    BaseRepository() {

    companion object {
        private const val TAG = "DownloadFromHuggingFaceNetworkRepositoryTag"
    }

    private val downloadManager: DownloadManager by inject()


    override fun downloadToExternalFilesDirectory(
        file: File,
        fileUrl: String,
        context: Context,
        huggingFaceToken: String?,
    ): Flow<ResultEmittedData<File>> = callbackFlow {
        logDebug("downloadToExternalFilesDirectory", TAG)
        var receiver: BroadcastReceiver? = null
        try {
            val destinationUri = file.toUri()
            val request = DownloadManager.Request(fileUrl.toUri()).apply {
                setTitle("File Download")
                setDescription("File downloading in progress")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationUri(destinationUri)
                huggingFaceToken?.let { token ->
                    addRequestHeader("Authorization", "Bearer $token")
                }
            }
            val downloadId = downloadManager.enqueue(request)
            receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                    if (id == downloadId) {
                        val query = DownloadManager.Query().setFilterById(downloadId)
                        val cursor = downloadManager.query(query)
                        if (cursor == null) {
                            logError("cursor == null", TAG)
                            trySend(
                                ResultEmittedData.error(
                                    model = null,
                                    error = null,
                                    title = "Download Error",
                                    message = "Download manager cursor is null",
                                    errorType = null,
                                    responseCode = null
                                )
                            )
                            return
                        }
                        if (cursor.moveToFirst()) {
                            val status = cursor.getInt(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                            )
                            when (status) {
                                DownloadManager.STATUS_RUNNING -> {
                                    logDebug("status == STATUS_RUNNING", TAG)
                                    val bytesDownloaded = cursor.getLong(
                                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                                    )
                                    val bytesTotal = cursor.getLong(
                                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                    )
                                    val progress = (bytesDownloaded * 100 / bytesTotal).toInt()
                                    trySend(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = progress.toString()
                                        )
                                    )
                                }

                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    logDebug("status == STATUS_SUCCESSFUL", TAG)
                                    val uriString = cursor.getString(
                                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                                    )
                                    val downloadedFile = File(uriString.toUri().path!!)
                                    trySend(
                                        ResultEmittedData.success(
                                            model = downloadedFile,
                                            responseCode = null,
                                            message = "Download completed successfully",
                                        )
                                    )
                                }

                                DownloadManager.STATUS_PENDING -> {
                                    logError("status == STATUS_PENDING", TAG)
                                    trySend(
                                        ResultEmittedData.error(
                                            model = null,
                                            error = null,
                                            title = "Download Error",
                                            message = "Download pending",
                                            errorType = null,
                                            responseCode = null
                                        )
                                    )
                                }

                                DownloadManager.STATUS_PAUSED -> {
                                    logError("status == STATUS_PAUSED", TAG)
                                    trySend(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = "Download paused"
                                        )
                                    )
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    logError("status == STATUS_FAILED", TAG)
                                    val reason = cursor.getInt(
                                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                                    )
                                    trySend(
                                        ResultEmittedData.error(
                                            model = null,
                                            error = null,
                                            title = "Download Error",
                                            message = "Download failed with reason: $reason",
                                            errorType = null,
                                            responseCode = null
                                        )
                                    )
                                }

                                else -> {
                                    logError("status unknown", TAG)
                                }
                            }
                        } else {
                            logError("cursor not moveToFirst", TAG)
                        }
                        close()
                    }
                }
            }
            val flags = ContextCompat.RECEIVER_NOT_EXPORTED
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                flags
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
            context.unregisterReceiver(receiver)
        }
    }.flowOn(Dispatchers.IO)

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