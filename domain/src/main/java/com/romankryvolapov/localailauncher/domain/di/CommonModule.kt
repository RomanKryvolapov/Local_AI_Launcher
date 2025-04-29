package com.romankryvolapov.localailauncher.domain.di

import ai.mlc.mlcllm.MLCEngine
import org.koin.dsl.module

val commonModules = module {

    single<MLCEngine>{
        MLCEngine()
    }

}