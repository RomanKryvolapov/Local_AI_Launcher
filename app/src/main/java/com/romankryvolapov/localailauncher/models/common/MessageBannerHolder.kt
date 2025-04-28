/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.view.View

interface MessageBannerHolder {

    fun showMessage(message: BannerMessage, anchorView: View? = null)

    fun showMessage(message: DialogMessage)

    fun showFullscreenLoader(message: StringSource?)

    fun hideFullscreenLoader()

}