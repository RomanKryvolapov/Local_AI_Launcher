/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import com.romankryvolapov.localailauncher.domain.models.common.OriginalModel
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommonTextFieldUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val required: Boolean,
    val question: Boolean,
    val text: StringSource,
    val title: StringSource,
    val error: StringSource? = null,
    val serverValue: String? = null,
    val originalModel: OriginalModel? = null,
    val isEnabled: Boolean = true,
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
            { text },
            { title },
            { error },
            { question },
            { required },
            { elementId },
            { elementEnum },
            { maxLinesText },
            { maxLinesTitle },
            { maxLinesError },
        )
    }

}