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
package com.romankryvolapov.localailauncher.domain.di

import com.romankryvolapov.localailauncher.domain.usecase.CopyAssetsToFileUseCase
import com.romankryvolapov.localailauncher.domain.usecase.DownloadFromHuggingFaceUseCase
import com.romankryvolapov.localailauncher.domain.usecase.SendMessageMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.SendMessageUseCase
import com.romankryvolapov.localailauncher.domain.usecase.StartEngineMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.StartEngineUseCase
import org.koin.dsl.module

val useCaseModule = module {

    factory<CopyAssetsToFileUseCase> {
        CopyAssetsToFileUseCase()
    }

    factory<StartEngineUseCase> {
        StartEngineUseCase()
    }

    factory<SendMessageUseCase> {
        SendMessageUseCase()
    }

    factory<DownloadFromHuggingFaceUseCase> {
        DownloadFromHuggingFaceUseCase()
    }

    factory<StartEngineMediaPipeUseCase> {
        StartEngineMediaPipeUseCase()
    }

    factory<SendMessageMediaPipeUseCase> {
        SendMessageMediaPipeUseCase()
    }

}