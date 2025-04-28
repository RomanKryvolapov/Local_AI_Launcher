/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.app.Dialog
import android.view.View
import androidx.annotation.ColorRes

@Suppress("DEPRECATION")
fun Dialog.setStatusBarColor(@ColorRes color: Int) {
    val realColor = context.color(color)
    window?.let { window ->
        var flags = window.decorView.systemUiVisibility
        if (!isDark(realColor)) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = realColor
    }
}