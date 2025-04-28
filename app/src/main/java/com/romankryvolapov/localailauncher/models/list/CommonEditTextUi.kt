/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable
import android.text.InputType
import androidx.annotation.ColorRes
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.models.base.TypeEnum
import com.romankryvolapov.localailauncher.domain.models.common.OriginalModel
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
data class CommonEditTextUi(
    override val elementId: Int? = null,
    override val elementEnum: CommonListElementIdentifier,
    val required: Boolean,
    val question: Boolean,
    val title: StringSource,
    val minSymbols: Int = 0,
    val maxSymbols: Int = 128,
    val selectedValue: String?,
    val hasFocus: Boolean = false,
    val isEnabled: Boolean = true,
    val hint: StringSource? = null,
    val error: StringSource? = null,
    val prefix: StringSource? = null,
    @ColorRes val prefixTextColor: Int? = null,
    val suffix: StringSource? = null,
    @ColorRes val suffixTextColor: Int? = null,
    val hasEraseButton: Boolean = false,
    val type: CommonEditTextUiType,
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
            { hint },
            { prefix },
            { suffix },
            { title },
            { error },
            { question },
            { required },
            { elementId },
            { selectedValue },
            { hasEraseButton },
            { prefixTextColor },
            { suffixTextColor }
        )
    }

}


enum class CommonEditTextUiType(
    override val type: String,
    val hint: StringSource,
    val inputType: CommonEditTextInputType
) : TypeEnum {
    EMAIL("EMAIL", StringSource(R.string.please_input), CommonEditTextInputType.EMAIL),
    PASSWORD("PASSWORD", StringSource(R.string.please_input), CommonEditTextInputType.PASSWORD),
    PASSWORD_NUMBERS("PASSWORD_NUMBERS", StringSource(R.string.please_input), CommonEditTextInputType.PASSWORD_NUMBERS),
    PHONE_NUMBER(
        "PHONE_NUMBER",
        StringSource(R.string.please_input),
        CommonEditTextInputType.PHONE
    ),
    NUMBERS("NUMBERS", StringSource(R.string.please_input), CommonEditTextInputType.DIGITS),
    TEXT_INPUT("INPUT", StringSource(R.string.please_input), CommonEditTextInputType.CHARS),
    TEXT_INPUT_CAP(
        "TEXT_NUMBER_CAP",
        StringSource(R.string.please_input),
        CommonEditTextInputType.CHARS_CAP
    )
}

enum class CommonEditTextInputType(
    override val type: String,
) : TypeEnum {
    CHARS("CHARS"),
    DIGITS("DIGITS"),
    CHARS_CAP("CHARS_CAP"),
    PHONE("PHONE"),
    PASSWORD("PASSWORD"),
    PASSWORD_NUMBERS("PASSWORD_NUMBERS"),
    EMAIL("EMAIL")
}