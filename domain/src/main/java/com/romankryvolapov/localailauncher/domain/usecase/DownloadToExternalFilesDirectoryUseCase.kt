/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase

import android.content.Context
import com.romankryvolapov.localailauncher.common.models.common.BaseUseCase
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.Model
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFileNetworkRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject
import java.io.File

class DownloadToExternalFilesDirectoryUseCase : BaseUseCase {

    companion object {
        private const val TAG = "DownloadToExternalFilesDirectoryUseCaseTag"
    }

    private val downloadRepository: DownloadFileNetworkRepository by inject()

    fun invoke(
        model: Model,
        context: Context,
    ): Flow<ResultEmittedData<File>> = downloadRepository.downloadWithManager(
        model = model,
        context = context,
    )

}