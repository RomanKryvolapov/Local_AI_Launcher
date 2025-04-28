/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class UiState : Parcelable {

    data object Ready : UiState()

    data object Empty : UiState()

}