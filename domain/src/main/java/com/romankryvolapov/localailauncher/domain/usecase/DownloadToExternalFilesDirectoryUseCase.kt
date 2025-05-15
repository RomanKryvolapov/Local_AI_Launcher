/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase

import android.content.Context
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFileNetworkRepository
import  com.romankryvolapov.localailauncher.common.models.common.BaseUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject
import java.io.File

class DownloadToExternalFilesDirectoryUseCase : BaseUseCase {

    companion object {
        private const val TAG = "DownloadToExternalFilesDirectoryUseCaseTag"
    }

    private val downloadRepository: DownloadFileNetworkRepository by inject()

    fun invoke(
        url: String,
        context: Context,
        subfolder: String,
        huggingFaceToken: String?,
    ): Flow<ResultEmittedData<File>> = downloadRepository.downloadToExternalFilesDirectory(
        fileUrl = url,
        context = context,
        subfolder = subfolder,
        huggingFaceToken = huggingFaceToken,
    )

}