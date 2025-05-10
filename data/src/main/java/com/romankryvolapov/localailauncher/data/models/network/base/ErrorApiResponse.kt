/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.models.network.base

import com.google.gson.annotations.SerializedName

data class ErrorApiResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("detail") val detail: String?,
) : ErrorResponse