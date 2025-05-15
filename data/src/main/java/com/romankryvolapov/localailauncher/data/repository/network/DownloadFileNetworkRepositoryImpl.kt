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
import com.romankryvolapov.localailauncher.data.repository.network.base.BaseRepository
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
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
        fileUrl: String,
        context: Context,
        subfolder: String,
        huggingFaceToken: String?
    ): Flow<ResultEmittedData<File>> = callbackFlow {

        trySend(ResultEmittedData.loading())

        val fileName = fileUrl.toUri().lastPathSegment

        if (fileName == null) {
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = null,
                    title = "Download Error",
                    message = "Unknown file name in url: $fileUrl",
                    errorType = null,
                    responseCode = null
                )
            )
            close()
            return@callbackFlow
        }

        val targetDir = File(context.getExternalFilesDir(null), subfolder).apply {
            if (!exists()) mkdirs()
        }
        val targetFile = File(targetDir, fileName)
        if (targetFile.exists()) {
            trySend(
                ResultEmittedData.success(
                    model = targetFile,
                    responseCode = null,
                    message = "File already exist: ${targetFile.absolutePath}"
                )
            )
            close()
            return@callbackFlow
        }

        val destinationUri = File(targetDir, fileName).toUri()

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

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    downloadManager.query(query)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val status = cursor.getInt(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                            )
                            when (status) {
                                DownloadManager.STATUS_RUNNING -> {
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
                                    val uriString = cursor.getString(
                                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                                    )
                                    val file = File(uriString.toUri().path!!)
                                    trySend(
                                        ResultEmittedData.success(
                                            model = file,
                                            responseCode = null,
                                            message = "Download completed successfully",
                                        )
                                    )
                                }

                                DownloadManager.STATUS_PENDING -> {
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
                                    trySend(
                                        ResultEmittedData.loading(
                                            model = null,
                                            message = "Download paused"
                                        )
                                    )
                                }

                                DownloadManager.STATUS_FAILED -> {
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


                            }
                        }
                        close()
                    }
                } else {
                    trySend(
                        ResultEmittedData.error(
                            model = null,
                            error = null,
                            title = "Download Error",
                            message = "Unknown download id: $id",
                            errorType = null,
                            responseCode = null
                        )
                    )
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