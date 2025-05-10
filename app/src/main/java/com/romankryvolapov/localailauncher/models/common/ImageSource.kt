/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

sealed interface ImageSource : Parcelable {

    @Parcelize
    data class Url(
        val url: String?,
        @DrawableRes val placeholder: Int,
        @DrawableRes val error: Int,
    ) : ImageSource

    @Parcelize
    data class Res(
        @DrawableRes val res: Int,
    ) : ImageSource

}