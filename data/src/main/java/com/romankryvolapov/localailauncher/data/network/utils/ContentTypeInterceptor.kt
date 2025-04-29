/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.network.utils

import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import okhttp3.Interceptor
import okhttp3.Response

class ContentTypeInterceptor : Interceptor {

    companion object {
        private const val TAG = "ContentTypeInterceptorTag"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val currentContentType = originalResponse.header("Content-Type")
        val source = originalResponse.body?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer
        val responseBodyString = buffer?.clone()?.readString(Charsets.UTF_8)
        val newContentType = when {
            responseBodyString?.trim()?.startsWith("{") == true -> "application/json"
            responseBodyString?.trim()?.startsWith("<") == true -> "application/xml"
            else -> null
        }
        return if (
            newContentType != null && (currentContentType == null || !currentContentType.contains(
                newContentType,
                ignoreCase = true
            ))
        ) {
            logDebug("Content type replace with $newContentType", TAG)
            originalResponse.newBuilder()
                .header("Content-Type", newContentType)
                .build()
        } else {
            originalResponse
        }
    }
}