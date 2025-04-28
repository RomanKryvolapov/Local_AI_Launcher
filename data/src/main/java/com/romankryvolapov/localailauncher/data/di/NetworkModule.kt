/**
 * single<Class>(named("name")){Class()} -> for creating a specific instance in module
 * single<Class1>{Class1(get<Class2>(named("name")))} -> for creating a specific instance in module
 * val nameOfVariable: Class by inject(named("name")) -> for creating a specific instance in class
 * get<Class>{parametersOf("param")} -> parameter passing in module
 * single<Class>{(param: String)->Class(param)} -> parameter passing in module
 * val name: Class by inject(parameters={parametersOf("param")}) -> parameter passing in class
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.google.gson.Gson
import org.koin.dsl.module

private const val TAG = "NetworkModuleTag"

const val RETROFIT_BASE = "RETROFIT_BASE"

const val OKHTTP = "OKHTTP"

const val LOGGING_INTERCEPTOR = "LOGGING_INTERCEPTOR"
const val LOG_TO_FILE_INTERCEPTOR = "LOG_TO_FILE_INTERCEPTOR"

const val TIMEOUT = 60L

val networkModule = module {


    single<Gson> {
        Gson()
    }

}