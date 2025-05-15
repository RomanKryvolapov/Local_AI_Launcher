/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.repository.network.DownloadFileNetworkRepositoryImpl
import com.romankryvolapov.localailauncher.domain.repository.network.DownloadFileNetworkRepository
import org.koin.dsl.module

val networkRepositoryModule = module {

    single<DownloadFileNetworkRepository> {
        DownloadFileNetworkRepositoryImpl()
    }

}