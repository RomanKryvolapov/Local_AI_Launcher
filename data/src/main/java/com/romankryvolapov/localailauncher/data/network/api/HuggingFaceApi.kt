/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface HuggingFaceApi {

    @Streaming
    @GET
    suspend fun downloadFile(
        @Url fileUrl: String,
        @Header("Authorization") authHeader: String,
    ): Response<ResponseBody>

}