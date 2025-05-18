package com.romankryvolapov.localailauncher.domain.usecase

import android.content.Context
import com.romankryvolapov.localailauncher.common.models.common.BaseUseCase
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.Model
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFileNetworkRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject
import java.io.File

class DownloadMultipleToExternalFilesDirectoryUseCase : BaseUseCase {

    companion object {
        private const val TAG = "DownloadMultipleToExternalFilesDirectoryUseCaseTag"
    }

    private val downloadRepository: DownloadFileNetworkRepository by inject()

    fun invoke(
        models: List<Model>,
        context: Context,
    ): Flow<ResultEmittedData<List<File>>> =
        downloadRepository.downloadDirectly(
            models = models,
            context = context,
        )

}