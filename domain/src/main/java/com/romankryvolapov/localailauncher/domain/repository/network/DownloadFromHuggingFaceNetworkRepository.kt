/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.repository.network

import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DownloadFromHuggingFaceNetworkRepository {

    fun downloadFromHuggingFace(
        fileUrl: String,
        huggingFaceToken: String,
    ): Flow<ResultEmittedData<File>>

}