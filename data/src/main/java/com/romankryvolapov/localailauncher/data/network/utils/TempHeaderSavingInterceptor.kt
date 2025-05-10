/**
 * The temporary interceptor to save a mock server steps header (locally, in memory)
 * and use it in next request in flow. Mostly used in flows like registration, login, etc.
 *
 * TODO should be deleted after removing the mock server with scenarios
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.network.utils

import okhttp3.Interceptor
import okhttp3.Response

class TempHeaderSavingInterceptor : Interceptor {

    companion object {
        private const val MOCK_STEP_HEADER_FLOW = "X-Flow"
        private const val MOCK_STEP_HEADER_STEP = "X-State"

        private var lastRequestHeaderFlow: String? = null
        private var lastRequestHeaderStep: String? = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        lastRequestHeaderFlow?.let {
            builder.header(MOCK_STEP_HEADER_FLOW, it)
        }

        lastRequestHeaderStep?.let {
            builder.header(MOCK_STEP_HEADER_STEP, it)
        }

        val req = builder.build()
        return chain.proceed(req).also { response ->
            response.headers[MOCK_STEP_HEADER_FLOW]?.let {
                lastRequestHeaderFlow = it
            }
            response.headers[MOCK_STEP_HEADER_STEP]?.let {
                lastRequestHeaderStep = it
            }
        }
    }
}