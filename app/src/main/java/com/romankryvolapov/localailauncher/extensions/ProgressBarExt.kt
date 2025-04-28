/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.animation.ObjectAnimator
import android.widget.ProgressBar

/**
 * Animate progress bar from old to new value
 */
fun ProgressBar.animateProgress(start: Int, stop: Int, duration: Long) {
    ObjectAnimator.ofInt(this, "progress", start, stop).apply {
        this.duration = duration
        start()
    }
}