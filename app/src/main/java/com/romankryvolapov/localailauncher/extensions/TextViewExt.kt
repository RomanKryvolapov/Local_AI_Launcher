/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ImageSpan
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.HtmlCompat
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.models.common.StringSource
import java.lang.reflect.Method

fun TextView.setTextSource(text: StringSource) {
    this.text = text.getString(context)
}

fun TextView.setSpannableOrHiddenText(
    isContentHidden: Boolean, tv: TextView,
    spannable: Spannable?
) {
    if (isContentHidden) {
        val hiddenPattern = "0000"
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        text = hiddenPattern
        tv.visibility = View.VISIBLE
    } else {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        text = spannable
        tv.visibility = View.GONE
    }
}

fun TextView.setAllCompoundDrawablesTint(@ColorRes color: Int) {
    for (drawable in compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter = PorterDuffColorFilter(
                context.color(color),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}

fun TextView.setCompoundDrawableTint(
    @ColorRes colorLeft: Int? = null,
    @ColorRes colorTop: Int? = null,
    @ColorRes colorRight: Int? = null,
    @ColorRes colorBottom: Int? = null,
) {
    compoundDrawables[0]?.let {
        if (colorLeft != null) {
            it.colorFilter = PorterDuffColorFilter(context.color(colorLeft), PorterDuff.Mode.SRC_IN)
        }
    }
    compoundDrawables[1]?.let {
        if (colorTop != null) {
            it.colorFilter = PorterDuffColorFilter(context.color(colorTop), PorterDuff.Mode.SRC_IN)
        }
    }
    compoundDrawables[2]?.let {
        if (colorRight != null) {
            it.colorFilter =
                PorterDuffColorFilter(context.color(colorRight), PorterDuff.Mode.SRC_IN)
        }
    }
    compoundDrawables[3]?.let {
        if (colorBottom != null) {
            it.colorFilter =
                PorterDuffColorFilter(context.color(colorBottom), PorterDuff.Mode.SRC_IN)
        }
    }
}

/**
 * Method allow us to listen clicks on right
 * compound drawable of the text view.
 */
@SuppressLint("ClickableViewAccessibility")
inline fun TextView.setDrawableRightTouch(
    crossinline clickListener: () -> Unit
) {
    val drawableRight = 2
    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN && event.rawX >= (right - compoundDrawables[drawableRight].bounds.width())) {
            clickListener.invoke()
            return@OnTouchListener true
        }
        false
    }
    )
}

/**
 * Method set text or set visibility invisible if text is null in TextView
 */
fun TextView.setTextOrInvisible(simpleText: String?) {
    when (simpleText) {
        null -> visibility = View.INVISIBLE
        else -> {
            text = simpleText
            visibility = View.VISIBLE
        }
    }
}

/**
 * Method set text or set visibility gone if text is null in TextView
 */
fun TextView.setTextOrGone(simpleText: String?) {
    when (simpleText) {
        null -> visibility = View.GONE
        else -> {
            text = simpleText
            visibility = View.VISIBLE
        }
    }
}

/**
 * Method set text or set visibility invisible if text is null in TextView
 */
fun TextView.setTextOrInvisible(spannableText: SpannableString?) {
    when (spannableText) {
        null -> visibility = View.INVISIBLE
        else -> {
            text = spannableText
            visibility = View.VISIBLE
        }
    }
}

/**
 * Method set text or set visibility gone if text is null in TextView
 */
fun TextView.setTextOrGone(spannableText: SpannableString?) {
    when (spannableText) {
        null -> visibility = View.GONE
        else -> {
            text = spannableText
            visibility = View.VISIBLE
        }
    }
}

/**
 * Method set text or set visibility invisible if text is null in TextView
 */
fun TextView.setTextOrInvisible(spannableText: SpannableStringBuilder?) {
    when (spannableText) {
        null -> visibility = View.INVISIBLE
        else -> {
            text = spannableText
            visibility = View.VISIBLE
        }
    }
}

/**
 * Method set text or set visibility gone if text is null in TextView
 */
fun TextView.setTextOrGone(spannableText: SpannableStringBuilder?) {
    when (spannableText) {
        null -> visibility = View.GONE
        else -> {
            text = spannableText
            visibility = View.VISIBLE
        }
    }
}

/**
 * Method set text from resource in TextView
 */
fun TextView.setTextResource(@StringRes resource: Int) {
    text = context.getString(resource)
}

/**
 * Method set text color from resource in TextView
 */
fun TextView.setTextColorResource(@ColorRes resource: Int) {
    setTextColor(context.color(resource))
}

fun TextView.setHintTextColorResource(@ColorRes resource: Int) {
    setHintTextColor(context.color(resource))
}

/**
 * Method set text as html in TextView
 */
fun TextView.setHtmlText(htmlText: String) {
    text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

/**
 * Method set text and set text underline in TextView
 */
fun TextView.setUnderlineText(
    underlineText: String,
    @ColorRes color: Int,
    thickness: Int,
) {
    val spannable = SpannableString(underlineText)
    spannable.setSpan(object : UnderlineSpan() {
        override fun updateDrawState(ds: TextPaint) {
            try {
                val method: Method = TextPaint::class.java.getMethod(
                    "setUnderlineText",
                    Integer.TYPE,
                    java.lang.Float.TYPE
                )
                method.invoke(
                    ds,
                    context.color(color),
                    thickness
                )
            } catch (e: Exception) {
                ds.isUnderlineText = true
            }
        }
    }, 0, spannable.length, 0)
    text = spannable
}

fun TextView.clearCompoundDrawables() {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}

fun TextView.setCompoundDrawablesExt(
    start: Drawable? = null,
    top: Drawable? = null,
    end: Drawable? = null,
    bottom: Drawable? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
}

fun TextView.setCompoundDrawablesExt(
    @DrawableRes start: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes end: Int = 0,
    @DrawableRes bottom: Int = 0,
) {
    setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
}

fun TextView.clearCompoundDrawablesExt() {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}

fun TextView.setTextWithIcon(
    textMaxLines: Int = 3,
    isClickable: Boolean = false,
    textStringSource: StringSource,
    @ColorRes textColorRes: Int? = null,
    @DrawableRes iconResLeft: Int? = null,
    @DrawableRes iconResRight: Int? = null,
) {
    try {
        val textString = textStringSource.getString(context)
        text = when {
            iconResLeft != null -> {
                ContextCompat.getDrawable(context, iconResLeft)?.let { drawable ->
                    if (textColorRes != null) {
                        val wrappedDrawable = DrawableCompat.wrap(drawable)
                        val color = ContextCompat.getColor(context, textColorRes)
                        DrawableCompat.setTint(wrappedDrawable, color)
                    }
                    val iconBounds = Rect(-2, -2, 46, 46)
                    drawable.bounds = iconBounds
                    SpannableStringBuilder().apply {
                        append(" ", ImageSpan(drawable), 0)
                        append(" ")
                        append(textString)
                        if (isClickable) {
                            setSpan(UnderlineSpan(), length - textString.length, length, 0)
                        }
                    }
                }
            }

            iconResRight != null -> {
                ContextCompat.getDrawable(context, iconResRight)?.let { drawable ->
                    if (textColorRes != null) {
                        val wrappedDrawable = DrawableCompat.wrap(drawable)
                        val color = ContextCompat.getColor(context, textColorRes)
                        DrawableCompat.setTint(wrappedDrawable, color)
                    }
                    val iconBounds = Rect(-2, -2, 46, 46)
                    drawable.bounds = iconBounds
                    SpannableStringBuilder().apply {
                        append(textString)
                        append(" ")
                        append(" ", ImageSpan(drawable), 0)
                        if (isClickable) {
                            setSpan(UnderlineSpan(), 0, textString.length, 0)
                        }
                    }
                }
            }

            else -> {
                if (isClickable) {
                    SpannableString(textString).apply {
                        setSpan(UnderlineSpan(), 0, textString.length, 0)
                    }
                } else {
                    textString
                }
            }
        }
        textColorRes?.let {
            setTextColorResource(it)
        }
        maxLines = textMaxLines
    } catch (e: Exception) {
        logError("refreshScreen", "TextViewExtTag")
    }
}