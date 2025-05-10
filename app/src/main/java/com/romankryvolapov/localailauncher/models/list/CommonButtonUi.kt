/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.ButtonColorUi
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonButtonUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val title: StringSource,
    val isEnabled: Boolean = true,
    val buttonColor: ButtonColorUi,
    @DrawableRes val iconResLeft: Int? = null,
    @DrawableRes val iconResRight: Int? = null,
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
            { isEnabled },
            { elementId },
            { elementEnum },
            { buttonColor },
            { iconResLeft },
            { iconResRight }
        )
    }

}