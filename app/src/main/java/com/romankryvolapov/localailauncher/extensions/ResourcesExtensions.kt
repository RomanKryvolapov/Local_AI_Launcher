/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.content.res.Resources
import android.util.DisplayMetrics

fun Resources.dpToPx(dp: Float): Float {
    return dp * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Resources.pxToDp(px: Float): Float {
    return px / (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}