/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.utils

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import com.romankryvolapov.localailauncher.domain.BuildConfig
import com.romankryvolapov.localailauncher.domain.models.log.LogData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

/**
 * This utility saves application logs to a file
 */

object LogUtil {

    private val timeDirectoryName: String

    private const val QUEUE_CAPACITY = 10000
    private const val CURRENT_TAG = "LogUtilExecutionStatusTag"
    private const val LOG_APP_FOLDER_NAME = "eid"
    private const val TIME_FORMAT_FOR_LOG = "HH:mm:ss dd-MM-yyyy"
    private const val TIME_FORMAT_FOR_DIRECTORY = "HH-mm-ss_dd-MM-yyyy"
    private const val TAG = "TAG: "
    private const val TIME = "TIME: "
    private const val ERROR_STACKTRACE = "ERROR STACKTRACE: "
    private const val ERROR_MESSAGE = "ERROR: "
    private const val DEBUG_MESSAGE = "MESSAGE: "
    private const val NEW_LINE = "\n"

    private val queue = ArrayDeque<LogData>(QUEUE_CAPACITY)

    private var saveLogsToTxtFileJob: Job? = null

    @Volatile
    private var isSaveLogsToTxtFile = false

    init {
        Log.d(CURRENT_TAG, "init")
        timeDirectoryName = getCurrentTimeForDirectory()
    }

    fun logDebug(message: String, tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                Log.d(tag, message)
                enqueue(
                    LogData.DebugMessage(
                        tag = tag,
                        time = System.currentTimeMillis(),
                        message = message,
                    )
                )
                saveLogsToTxtFile()
            }
        }
    }

    fun logError(message: String, tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                Log.e(tag, message)
                enqueue(
                    LogData.ErrorMessage(
                        tag = tag,
                        time = System.currentTimeMillis(),
                        message = message,
                    )
                )
                saveLogsToTxtFile()
            }
        }
    }

    fun logError(exception: Throwable, tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                Log.e(tag, exception.message, exception)
                enqueue(
                    LogData.ExceptionMessage(
                        tag = tag,
                        time = System.currentTimeMillis(),
                        exception = exception,
                    )
                )
                saveLogsToTxtFile()
            }
        }
    }

    fun logError(message: String, exception: Throwable, tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                Log.e(tag, "$message, exception: ${exception.message}", exception)
                enqueue(
                    LogData.ErrorMessageWithException(
                        tag = tag,
                        time = System.currentTimeMillis(),
                        message = message,
                        exception = exception,
                    )
                )
                saveLogsToTxtFile()
            }
        }
    }

    fun logError(message: String, error: String?, tag: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                Log.e(tag, "$message, error: $error")
                enqueue(
                    LogData.ErrorMessage(
                        tag = tag,
                        time = System.currentTimeMillis(),
                        message = message,
                    )
                )
                saveLogsToTxtFile()
            }
        }
    }

    fun logNetwork(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (BuildConfig.DEBUG) {
                enqueue(
                    LogData.NetworkMessage(
                        time = System.currentTimeMillis(),
                        message = message,
                    )
                )
                saveLogsToTxtFile()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(time: Long): String {
        return try {
            val date = Date(time)
            val timeString = SimpleDateFormat(TIME_FORMAT_FOR_LOG).format(date)
            timeString.ifEmpty {
                Log.e(CURRENT_TAG, "getTime time.ifEmpty")
                time.toString()
            }
        } catch (e: Exception) {
            Log.e(CURRENT_TAG, "getCurrentTime exception: ${e.message}", e)
            time.toString()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTimeForDirectory(): String {
        val time = System.currentTimeMillis()
        return try {
            val date = Date(time)
            val timeString = SimpleDateFormat(TIME_FORMAT_FOR_DIRECTORY).format(date)
            Log.d(CURRENT_TAG, "getCurrentTimeForDirectory time: $time")
            timeString.ifEmpty {
                Log.e(CURRENT_TAG, "getCurrentTimeForDirectory time.ifEmpty")
                time.toString()
            }
        } catch (e: Exception) {
            Log.e(CURRENT_TAG, "getCurrentTimeForDirectory exception: ${e.message}", e)
            time.toString()
        }
    }

    private fun enqueue(message: LogData) {
        try {
            while (queue.size >= QUEUE_CAPACITY) {
                Log.d(CURRENT_TAG, "enqueue removeFirst")
                queue.removeFirst()
            }
            queue.addLast(message)
        } catch (e: Exception) {
            Log.e(CURRENT_TAG, "enqueue exception: ${e.message}", e)
        }
    }

    private fun saveLogsToTxtFile() {
        if (isSaveLogsToTxtFile) return
        isSaveLogsToTxtFile = true
        saveLogsToTxtFileJob?.cancel()
        saveLogsToTxtFileJob = null
        saveLogsToTxtFileJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .path
                val rootDirectory = File(path)
                if (!rootDirectory.exists()) {
                    val created = rootDirectory.mkdirs()
                    if (!created) {
                        Log.e(CURRENT_TAG, "Root log directory not created")
                        isSaveLogsToTxtFile = false
                        return@launch
                    }
                }
                val appDirectory = File(path, LOG_APP_FOLDER_NAME)
                if (!appDirectory.exists()) {
                    val created = appDirectory.mkdirs()
                    if (!created) {
//                        Log.e(CURRENT_TAG, "App log directory not created")
                        isSaveLogsToTxtFile = false
                        return@launch
                    }
                }
                val timeDirectory = File(appDirectory, timeDirectoryName)
                if (!timeDirectory.exists()) {
                    val created = timeDirectory.mkdirs()
                    if (!created) {
                        Log.e(CURRENT_TAG, "App time directory not created")
                        isSaveLogsToTxtFile = false
                        return@launch
                    }
                }
                val fileAll = File(timeDirectory, "LOG.txt")
                if (!fileAll.exists()) {
                    val created = fileAll.createNewFile()
                    if (!created) {
                        Log.e(CURRENT_TAG, "App log file not created")
                    }
                }

                val currentQueue = ArrayDeque(queue)
                var text: String? = buildString {
                    currentQueue.forEach {
                        when (it) {
                            is LogData.DebugMessage -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(DEBUG_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(NEW_LINE)
                            }

                            is LogData.ErrorMessage -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(NEW_LINE)
                            }

                            is LogData.ExceptionMessage -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_STACKTRACE)
                                it.exception.stackTrace.forEach { element ->
                                    append(element.toString())
                                    append(NEW_LINE)
                                }
                                append(NEW_LINE)
                            }

                            is LogData.ErrorMessageWithException -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(ERROR_STACKTRACE)
                                it.exception.stackTrace.forEach { element ->
                                    append(element.toString())
                                    append(NEW_LINE)
                                }
                                append(NEW_LINE)
                            }

                            is LogData.NetworkMessage -> {
                                append(TAG)
                                append("OkHttpClient")
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(DEBUG_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(NEW_LINE)
                            }

                            is LogData.ErrorMessageWithData -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(NEW_LINE)
                            }

                             else -> {
                                 // nothing
                             }
                        }
                    }
                }
                FileOutputStream(fileAll).use { outputStream ->
                    outputStream.write(text!!.toByteArray())
                    outputStream.flush()
                }
                Log.d(
                    CURRENT_TAG,
                    "Save logs size: ${text?.length}"
                )

                val fileErrors = File(timeDirectory, "LOG_ERRORS.txt")
                if (!fileErrors.exists()) {
                    val created = fileErrors.createNewFile()
                    if (!created) {
                        Log.e(CURRENT_TAG, "App log error file not created")
                    }
                }
                text = buildString {
                    currentQueue.filter {
                        it is LogData.ErrorMessage ||
                                it is LogData.ExceptionMessage ||
                                it is LogData.ErrorMessageWithException
                    }.forEach {
                        when (it) {
                            is LogData.ErrorMessage -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(NEW_LINE)
                            }

                            is LogData.ExceptionMessage -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_STACKTRACE)
                                it.exception.stackTrace.forEach { element ->
                                    append(element.toString())
                                    append(NEW_LINE)
                                }
                                append(NEW_LINE)
                            }

                            is LogData.ErrorMessageWithException -> {
                                append(TAG)
                                append(it.tag)
                                append(NEW_LINE)
                                append(TIME)
                                append(getTime(it.time))
                                append(NEW_LINE)
                                append(ERROR_MESSAGE)
                                append(it.message)
                                append(NEW_LINE)
                                append(ERROR_STACKTRACE)
                                it.exception.stackTrace.forEach { element ->
                                    append(element.toString())
                                    append(NEW_LINE)
                                }
                                append(NEW_LINE)
                            }

                            else -> {
                                // nothing
                            }

                        }
                    }
                }
                FileOutputStream(fileErrors).use { outputStream ->
                    outputStream.write(text!!.toByteArray())
                    outputStream.flush()
                }

                val fileNetwork = File(timeDirectory, "LOG_NETWORK.txt")
                if (!fileNetwork.exists()) {
                    val created = fileNetwork.createNewFile()
                    if (!created) {
                        Log.e(CURRENT_TAG, "App log network file not created")
                    }
                }
                text = buildString {
                    currentQueue.filterIsInstance<LogData.NetworkMessage>()
                        .forEach {
                            append(getTime(it.time))
                            append(NEW_LINE)
                            append(it.message)
                            append(NEW_LINE)
                            append(NEW_LINE)
                        }
                }
                FileOutputStream(fileNetwork).use { outputStream ->
                    outputStream.write(text!!.toByteArray())
                    outputStream.flush()
                }

                text = null
            } catch (e: Exception) {
                Log.e(CURRENT_TAG, "saveLogsToTxtFile exception: ${e.message}", e)
            }
            isSaveLogsToTxtFile = false
        }
    }

}