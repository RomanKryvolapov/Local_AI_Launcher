/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonSeparatorUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier? = null,
) : CommonListElementAdapterMarker, Parcelable {

    override fun isItemSame(other: Any?): Boolean {
        return equalTo(other)
    }

    override fun isContentSame(other: Any?): Boolean {
        return equalTo(other)
    }

}