/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.infrastructure.LaunchEngines
import org.koin.dsl.module

val commonModule = module {

    single<LaunchEngines> {
        LaunchEngines()
    }

}