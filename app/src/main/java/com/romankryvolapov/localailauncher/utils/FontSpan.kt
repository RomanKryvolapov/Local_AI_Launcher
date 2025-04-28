/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

open class FontSpan(private val font: Typeface) : MetricAffectingSpan() {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, font)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, font)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        paint.typeface = tf
    }
}