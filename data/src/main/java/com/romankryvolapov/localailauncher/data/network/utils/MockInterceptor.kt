/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.network.utils

import com.romankryvolapov.localailauncher.data.BuildConfig
import com.romankryvolapov.localailauncher.domain.DEBUG_MOCK_INTERCEPTOR_ENABLED
import com.romankryvolapov.localailauncher.domain.mockResponses
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {

    companion object {
        private const val TAG = "MockInterceptorTag"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val mockResponse = mockResponses[request.url.toString()]
        return if (BuildConfig.DEBUG &&
            DEBUG_MOCK_INTERCEPTOR_ENABLED &&
            mockResponse != null &&
            mockResponse.isEnabled
        ) {
            logDebug("Intercepted request: ${request.url}", TAG)
            Response.Builder()
                .code(mockResponse.serverCode)
                .message(mockResponse.message)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(
                    mockResponse.body
                        .toByteArray()
                        .toResponseBody(mockResponse.contentType.toMediaTypeOrNull())
                )
                .addHeader("Content-type", "application/json")
                .build()
        } else {
            chain.proceed(request)
        }
    }

}