/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.graphics.drawable.Drawable

fun Drawable.startSafe() {
    // Because of strange bug on Meizu Device
    // android.graphics.drawable.AnimatedVectorDrawable cannot be cast to
    // androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
    (this as? android.graphics.drawable.AnimatedVectorDrawable)?.start()
    (this as? androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat)?.start()
}