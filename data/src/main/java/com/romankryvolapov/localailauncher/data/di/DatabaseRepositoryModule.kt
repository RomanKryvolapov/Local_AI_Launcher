/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.repository.database.ChatDialogDatabaseRepositoryImpl
import com.romankryvolapov.localailauncher.domain.repository.database.ChatDialogDatabaseRepository
import org.koin.dsl.module

val databaseRepositoryModule = module {

    single<ChatDialogDatabaseRepository> {
        ChatDialogDatabaseRepositoryImpl()
    }

}