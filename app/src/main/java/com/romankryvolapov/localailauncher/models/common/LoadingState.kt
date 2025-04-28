/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class LoadingState : Parcelable {

    data class Loading(
        val message: String?,
        val translucent: Boolean
    ) : LoadingState()

    data object Ready : LoadingState()

}