/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.repository.network.DownloadFromHuggingFaceNetworkRepositoryImpl
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFromHuggingFaceNetworkRepository
import org.koin.dsl.module

val networkRepositoryModule = module {

    single<DownloadFromHuggingFaceNetworkRepository> {
        DownloadFromHuggingFaceNetworkRepositoryImpl()
    }

}