/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.repository.network

import android.content.Context
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DownloadFileNetworkRepository {

    fun downloadToExternalFilesDirectory(
        fileUrl: String,
        context: Context,
        subfolder: String,
        huggingFaceToken: String?,
    ): Flow<ResultEmittedData<File>>

}