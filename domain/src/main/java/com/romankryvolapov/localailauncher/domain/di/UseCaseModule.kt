/**
 * single<Class>(named("name")){Class()} -> for creating a specific instance in module
 * single<Class1>{Class1(get<Class2>(named("name")))} -> for creating a specific instance in module
 * val nameOfVariable: Class by inject(named("name")) -> for creating a specific instance in class
 * get<Class>{parametersOf("param")} -> parameter passing in module
 * single<Class>{(param: String)->Class(param)} -> parameter passing in module
 * val name: Class by inject(parameters={parametersOf("param")}) -> parameter passing in class
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.di

import com.romankryvolapov.localailauncher.domain.usecase.CopyAllAssetFilesToUserFilesUseCase
import com.romankryvolapov.localailauncher.domain.usecase.CopyAssetFileToUserFilesUseCase
import com.romankryvolapov.localailauncher.domain.usecase.DownloadFromHuggingFaceUseCase
import com.romankryvolapov.localailauncher.domain.usecase.llama.SendMessageLLamaCppEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.llama.StartLLamaCppEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mediapipe.SendMessageMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mediapipe.StartEngineMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mlcllm.SendMessageMLCEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mlcllm.StartMLCEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.onnx.SendMessageOnnxEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.onnx.StartOnnxEngineUseCase
import org.koin.dsl.module

val useCaseModule = module {

    factory<CopyAssetFileToUserFilesUseCase> {
        CopyAssetFileToUserFilesUseCase()
    }

    factory<CopyAllAssetFilesToUserFilesUseCase> {
        CopyAllAssetFilesToUserFilesUseCase()
    }

    factory<StartMLCEngineUseCase> {
        StartMLCEngineUseCase()
    }

    factory<SendMessageMLCEngineUseCase> {
        SendMessageMLCEngineUseCase()
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

    factory<StartOnnxEngineUseCase> {
        StartOnnxEngineUseCase()
    }

    factory<SendMessageOnnxEngineUseCase> {
        SendMessageOnnxEngineUseCase()
    }

    factory<StartLLamaCppEngineUseCase> {
        StartLLamaCppEngineUseCase()
    }

    factory<SendMessageLLamaCppEngineUseCase> {
        SendMessageLLamaCppEngineUseCase()
    }

}