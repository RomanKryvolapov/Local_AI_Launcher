/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.network.utils.NullOrEmptyConverterFactory
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

private const val URL_BASE = "https://huggingface.co/"

val retrofitModule = module {

    single<Retrofit>(named(RETROFIT_BASE)) {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(get<OkHttpClient>(named(OKHTTP)))
//            .addConverterFactory(get<ArrayConverterFactory>())
            .addConverterFactory(get<NullOrEmptyConverterFactory>())
            .addConverterFactory(get<GsonConverterFactory>())
            .addConverterFactory(get<SimpleXmlConverterFactory>())
            .build()
    }

}