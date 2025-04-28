/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ErrorState : Parcelable {

    data object Ready : ErrorState()

    data class Error(
        val title: StringSource,
        val description: StringSource,
        val iconRes: Int?,
        val showIcon: Boolean?,
        val showTitle: Boolean?,
        val showDescription: Boolean?,
        val showActionOneButton: Boolean?,
        val showActionTwoButton: Boolean?,
        val actionOneButtonText: StringSource?,
        val actionTwoButtonText: StringSource?,
    ) : ErrorState()

}