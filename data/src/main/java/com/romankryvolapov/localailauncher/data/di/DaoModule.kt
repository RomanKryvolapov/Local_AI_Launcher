/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.database.AppDatabase
import com.romankryvolapov.localailauncher.data.database.dao.ChatDialogDao
import com.romankryvolapov.localailauncher.data.database.dao.ChatMessageDao
import org.koin.dsl.module

val daoModule = module {

    factory<ChatDialogDao> {
        get<AppDatabase>().getChatDialogDao()
    }

    factory<ChatMessageDao> {
        get<AppDatabase>().getChatMessageDao()
    }

}