/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonTitleBigUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier? = null,
    val title: StringSource,
    val description: StringSource?,
    val maxLinesTitle: Int = 2,
    val maxLinesDescription: Int = 3,
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
            { description },
            { maxLinesTitle },
            { maxLinesDescription },
        )
    }

}