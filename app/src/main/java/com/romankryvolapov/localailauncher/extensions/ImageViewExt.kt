/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView

fun ImageView.tintRes(@ColorRes colorRes: Int) {
    setColorFilter(
        context.color(colorRes),
        PorterDuff.Mode.SRC_IN
    )
}

fun ImageView.tintColor(@ColorInt color: Int) {
    setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun AppCompatImageView.tintRes(@ColorRes colorRes: Int) {
    tintParsed(context.color(colorRes))
}

fun ImageView.tintParsed(@ColorRes colorRes: Int) {
    setColorFilter(colorRes, PorterDuff.Mode.SRC_IN)
}