/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.splash

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class SplashLoadingMessageUi(
    val id: UUID = UUID.randomUUID(),
    val message: String
) : SplashLoadingMessageAdapterMarker, Parcelable {

    override fun isItemSame(other: Any?): Boolean {
        return equalTo(
            other,
            { id },
        )
    }

    override fun isContentSame(other: Any?): Boolean {
        return equalTo(
            other,
            { id },
            { message },
        )
    }

}