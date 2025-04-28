/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.utils.CurrentContext
import kotlinx.parcelize.Parcelize

@Parcelize
class StringSource private constructor(
    private val text: String? = null,
    @StringRes private val resId: Int? = null,
    private val formatArgs: List<String>? = null,
    private val sources: List<StringSource>? = null,
    private val separator: CharSequence = ", ",
) : Parcelable {

    constructor(sources: List<StringSource>, separator: CharSequence) : this(
        sources = sources,
        separator = separator,
        text = null,
        resId = null,
        formatArgs = null
    )

    constructor(text: String?) : this(text = text, resId = null, formatArgs = null)

    constructor(text: String?, formatArgs: List<String>?) : this(
        text = text,
        resId = null,
        formatArgs = formatArgs
    )

    constructor(@StringRes resId: Int) : this(text = null, resId = resId, formatArgs = null)

    constructor(@StringRes resId: Int, formatArgs: List<String>?) : this(
        text = null,
        resId = resId,
        formatArgs = formatArgs
    )

    fun getString(context: Context): String {
        return when {
            text != null -> formatArgs?.let { text.format(*(it.toTypedArray())) } ?: text
            resId != null -> formatArgs?.let { context.getString(resId, *(it.toTypedArray())) }
                ?: context.getString(resId)
            sources != null -> sources.joinToString(separator) { it.getString(context) }
            else -> context.getString(R.string.unknown)
        }
    }

    fun getString(context: CurrentContext): String {
        return when {
            text != null -> formatArgs?.let { text.format(*(it.toTypedArray())) } ?: text
            resId != null -> formatArgs?.let { context.getString(resId, it) }
                ?: context.getString(resId)
            sources != null -> sources.joinToString(separator) { it.getString(context) }
            else -> context.getString(R.string.unknown)
        }
    }

    // TODO

    fun addTextEnd(text: String): StringSource {
        return StringSource(
            text = this.text + text,
            resId = this.resId,
            formatArgs = this.formatArgs
        )
    }

    fun addTextStart(text: String): StringSource {
        return StringSource(
            text = text + this.text,
            resId = this.resId,
            formatArgs = this.formatArgs
        )
    }

    fun addTextEnd(text: StringSource): StringSource {
        return StringSource(
            text = this.text + text.text,
            resId = this.resId,
            formatArgs = this.formatArgs
        )
    }

    fun addTextStart(text: StringSource): StringSource {
        return StringSource(
            text = text.text + this.text,
            resId = this.resId,
            formatArgs = this.formatArgs
        )
    }
}
