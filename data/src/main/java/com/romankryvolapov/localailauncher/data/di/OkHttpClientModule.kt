/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.di

import com.romankryvolapov.localailauncher.data.BuildConfig
import com.romankryvolapov.localailauncher.domain.DEBUG_MOCK_INTERCEPTOR_ENABLED
import com.romankryvolapov.localailauncher.data.network.utils.ContentTypeInterceptor
import com.romankryvolapov.localailauncher.data.network.utils.HeaderInterceptor
import com.romankryvolapov.localailauncher.data.network.utils.MockInterceptor
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val TAG = "OkHttpClientModuleTag"

val okHttpClientModule = module {

    single<OkHttpClient>(named(OKHTTP)) {
        logDebug("create OkHttpClient PG)", TAG)
        OkHttpClient.Builder().apply {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                    // NO IMPLEMENTATION
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                    // NO IMPLEMENTATION
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            )
            val protocolSSL = "SSL"
            val sslContext = SSLContext.getInstance(protocolSSL).apply {
                init(null, trustAllCerts, SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            followRedirects(true)
            followSslRedirects(true)
            addInterceptor(get<ContentTypeInterceptor>())
            addInterceptor(get<HeaderInterceptor>())
            addInterceptor(get<HttpLoggingInterceptor>(named(LOGGING_INTERCEPTOR)))
            addInterceptor(get<HttpLoggingInterceptor>(named(LOG_TO_FILE_INTERCEPTOR)))
            if (BuildConfig.DEBUG && DEBUG_MOCK_INTERCEPTOR_ENABLED) {
                logDebug("add MockInterceptor", TAG)
                addInterceptor(get<MockInterceptor>())
            }
            connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            readTimeout(TIMEOUT, TimeUnit.SECONDS)
        }.build()
    }
    
}