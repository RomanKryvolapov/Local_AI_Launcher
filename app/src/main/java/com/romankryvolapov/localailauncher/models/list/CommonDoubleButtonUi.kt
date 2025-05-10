/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.ButtonColorUi
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonDoubleButtonUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val firstButton: CommonDoubleButtonItem,
    val secondButton: CommonDoubleButtonItem,
    val selectedIdentifier: CommonListElementIdentifier? = null,
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
            { elementId },
            { elementEnum },
            { firstButton },
            { secondButton },
            { selectedIdentifier },
        )
    }

}

@Parcelize
data class CommonDoubleButtonItem(
    val title: StringSource,
    val buttonColor: ButtonColorUi,
    val identifier: CommonListElementIdentifier,
) : Parcelable