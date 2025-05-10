/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardScanBottomSheetContent(
    val cardCurrentPin: String,
    val cardNewPin: String? = null,
) : Parcelable
