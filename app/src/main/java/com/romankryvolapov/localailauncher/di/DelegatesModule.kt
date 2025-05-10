/**
 * single<Class>(named("name")){Class()} -> for creating a specific instance in module
 * single<Class1>{Class1(get<Class2>(named("name")))} -> for creating a specific instance in module
 * val nameOfVariable: Class by inject(named("name")) -> for creating a specific instance in class
 * get<Class>{parametersOf("param")} -> parameter passing in module
 * single<Class>{(param: String)->Class(param)} -> parameter passing in module
 * val name: Class by inject(parameters={parametersOf("param")}) -> parameter passing in class
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.di

import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list.ChatMessagesErrorDelegate
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list.ChatMessagesModelDelegate
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list.ChatMessagesUserDelegate
import org.koin.dsl.module

val delegatesModule = module {

    single<ChatMessagesUserDelegate> {
        ChatMessagesUserDelegate()
    }

    single<ChatMessagesModelDelegate> {
        ChatMessagesModelDelegate()
    }

    single<ChatMessagesErrorDelegate> {
        ChatMessagesErrorDelegate()
    }

}