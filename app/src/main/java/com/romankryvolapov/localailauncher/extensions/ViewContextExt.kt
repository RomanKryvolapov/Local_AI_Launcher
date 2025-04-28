/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ContextThemeWrapper
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

fun Context.color(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)

fun Context.drawableByStyle(@DrawableRes drawableRes: Int, @StyleRes styleRes: Int): Drawable? {
    return ContextThemeWrapper(this, styleRes).let { themeWrapper ->
        ResourcesCompat.getDrawable(resources, drawableRes, themeWrapper.theme)
    }
}

fun Context?.pxDimen(@DimenRes dimenRes: Int): Int {
    return this?.resources?.getDimensionPixelSize(dimenRes) ?: 0
}

fun Context?.dpDimen(@DimenRes dimenRes: Int): Float {
    return if (this != null) {
        pxDimen(dimenRes) / resources.displayMetrics.density
    } else 0f
}