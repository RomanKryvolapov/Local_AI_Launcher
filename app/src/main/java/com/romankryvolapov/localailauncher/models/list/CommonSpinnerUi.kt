/**
 * Created 2025 by Roman Kryvolapov
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
data class CommonSpinnerUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val required: Boolean? = null,
    val question: Boolean? = null,
    val title: StringSource? = null,
    val error: StringSource? = null,
    val hasEraseButton: Boolean? = false,
    val list: List<CommonSpinnerMenuItemUi>,
    val selectedValue: CommonSpinnerMenuItemUi?,
    val maxLinesTitle: Int = 2,
    val maxLinesText: Int = 1,
    val maxLinesError: Int = 2,
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
            { list },
            { title },
            { error },
            { question },
            { required },
            { elementId },
            { elementEnum },
            { maxLinesText },
            { selectedValue },
            { maxLinesTitle },
            { maxLinesError },
            { hasEraseButton },
        )
    }

}

@Parcelize
data class CommonSpinnerMenuItemUi(
    val id: Int? = null,
    val text: StringSource,
    val isSelected: Boolean,
    val serverValue: String? = null,
    @DrawableRes val iconRes: Int? = null,
    @ColorRes val iconColorRes: Int? = null,
    val originalModel: OriginalModel? = null,
    @ColorRes val textColorRes: Int = R.color.color_1C3050,
    val elementEnum: CommonListElementIdentifier? = null,
    val maxLines: Int = 1,
) : Parcelable