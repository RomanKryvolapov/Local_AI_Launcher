/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApplicationInfo(
    val isFirstFun: Boolean,
    val applicationLanguage: ApplicationLanguage,
) : Parcelable