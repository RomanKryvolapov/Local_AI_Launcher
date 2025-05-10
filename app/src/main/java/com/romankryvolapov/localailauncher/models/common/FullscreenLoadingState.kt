/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class FullscreenLoadingState : Parcelable {

    data class Loading(
        val message: StringSource?,
    ) : FullscreenLoadingState()

    data object Ready : FullscreenLoadingState()

}