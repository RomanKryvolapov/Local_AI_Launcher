/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import com.romankryvolapov.localailauncher.domain.models.common.OriginalModel
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.DiffEquals
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

sealed interface CommonDialogWithSearchAdapterMarker : DiffEquals

@Parcelize
data class CommonDialogWithSearchUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val required: Boolean,
    val question: Boolean,
    val title: StringSource,
    val error: StringSource? = null,
    val hasEraseButton: Boolean = false,
    val customInputEnabled: Boolean = false,
    val list: List<CommonDialogWithSearchItemUi>,
    val selectedValue: CommonDialogWithSearchItemUi?,
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
            { required },
            { question },
            { elementId },
            { elementEnum },
            { maxLinesText },
            { selectedValue },
            { maxLinesTitle },
            { maxLinesError },
            { hasEraseButton },
            { customInputEnabled },
        )
    }

}

@Parcelize
data class CommonDialogWithSearchItemUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier? = null,
    val text: StringSource,
    val serverValue: String? = null,
    val originalModel: OriginalModel? = null,
    val maxLines: Int = 1,
) : CommonListElementAdapterMarker, CommonDialogWithSearchAdapterMarker, Parcelable {


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
            { maxLines },
            { elementId },
            { serverValue },
            { elementEnum },
        )
    }
}
