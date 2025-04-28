/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DownloadProgress : Parcelable {

    data class Loading(
        val message: String? = null,
    ) : DownloadProgress()

    data object Ready : DownloadProgress()

}