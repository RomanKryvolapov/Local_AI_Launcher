/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.network.api.HuggingFaceApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import kotlin.jvm.java


const val HUGGING_FACE_API = "HUGGING_FACE_API"

val apiModule = module {

    single {
        get<Retrofit>(named(HUGGING_FACE_API)).create(HuggingFaceApi::class.java)
    }

}