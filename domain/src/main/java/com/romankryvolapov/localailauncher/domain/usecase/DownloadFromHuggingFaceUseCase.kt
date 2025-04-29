/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase

import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.models.base.onFailure
import com.romankryvolapov.localailauncher.domain.models.base.onSuccess
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFromHuggingFaceNetworkRepository
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.inject
import java.io.File

class DownloadFromHuggingFaceUseCase : BaseUseCase {

    companion object {
        private const val TAG = "DownloadFromHuggingFaceUseCaseTag"
    }

    private val downloadRepository: DownloadFromHuggingFaceNetworkRepository by inject()

    fun invoke(
        url: String,
        filesDir: File,
        fileName: String,
        huggingFaceToken: String,
    ): Flow<ResultEmittedData<File>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        val file = File(filesDir, fileName)
        if (file.exists()) {
            emit(
                ResultEmittedData.success(
                    model = file,
                    responseCode = null,
                    message = "Already downloaded",
                )
            )
            return@flow
        }
        downloadRepository.downloadFromHuggingFace(
            fileUrl = url,
            huggingFaceToken = huggingFaceToken,
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                emit(
                    ResultEmittedData.success(
                        model = model,
                        responseCode = responseCode,
                        message = "Downloaded successfully",
                    )
                )
            }.onFailure { error, title, message, responseCode, errorType ->
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
        }
    }
}