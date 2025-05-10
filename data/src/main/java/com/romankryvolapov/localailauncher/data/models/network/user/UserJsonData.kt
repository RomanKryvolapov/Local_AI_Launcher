/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.models.network.user

import com.google.gson.annotations.SerializedName

data class UserJsonData(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
)