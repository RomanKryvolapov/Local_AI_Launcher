/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.repository.network

import android.content.Context
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.Model
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DownloadFileNetworkRepository {

    fun downloadWithManager(
        model: Model,
        context: Context,
    ): Flow<ResultEmittedData<File>>

    fun downloadWithManager(
        context: Context,
        models: List<Model>,
    ): Flow<ResultEmittedData<List<File>>>

    fun downloadDirectly(
        context: Context,
        models: List<Model>,
    ): Flow<ResultEmittedData<List<File>>>

}