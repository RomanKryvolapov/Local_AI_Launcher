/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.models.common.OriginalModel
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonSimpleTextUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier? = null,
    val maxLines: Int = 3,
    val text: StringSource,
    val title: StringSource,
    val isClickable: Boolean = false,
    @ColorRes val colorRes: Int = R.color.color_1C3050,
    @DrawableRes val iconResLeft: Int? = null,
    @DrawableRes val iconResRight: Int? = null,
    val originalModel: OriginalModel? = null,
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
            { text },
            { title },
            { maxLines },
            { elementId },
            { elementEnum },
            { colorRes },
        )
    }

}