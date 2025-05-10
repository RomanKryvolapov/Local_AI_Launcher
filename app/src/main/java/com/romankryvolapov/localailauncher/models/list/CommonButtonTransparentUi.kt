/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonButtonTransparentUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val title: StringSource,
) : CommonListElementAdapterMarker, Parcelable {

    override fun isItemSame(other: Any?): Boolean {
        return equalTo(
            other,
            { elementId },
            { elementEnum },
        )
    }

    override fun isContentSame(other: Any?): Boolean {
        return equalTo(
            other,
            { title },
            { elementId },
            { elementEnum },
        )
    }

}
