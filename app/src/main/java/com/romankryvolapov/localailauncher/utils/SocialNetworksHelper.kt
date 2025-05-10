/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.romankryvolapov.localailauncher.extensions.openUrlInBrowser
import com.romankryvolapov.localailauncher.extensions.safeStartActivity

class SocialNetworksHelper {
    companion object {
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        private const val TIKTOK_PACKAGE = "com.zhiliaoapp.musically"
        private const val TWITTER_PACKAGE = "com.twitter.android"
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
    }

    fun openInstagramUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.`package` = INSTAGRAM_PACKAGE
        openInAppOrInBrowser(context, url, intent)
    }

    fun openFacebookUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.`package` = FACEBOOK_PACKAGE
        openInAppOrInBrowser(context, url, intent)
    }

    fun openYoutubeUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.`package` = YOUTUBE_PACKAGE
        openInAppOrInBrowser(context, url, intent)
    }

    fun openTiktokUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.`package` = TIKTOK_PACKAGE
        openInAppOrInBrowser(context, url, intent)
    }

    fun openTwitterUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.`package` = TWITTER_PACKAGE
        openInAppOrInBrowser(context, url, intent)
    }

    fun openWhatsAppPhoneNumber(context: Context, phoneNumber: String) {
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.`package` = WHATSAPP_PACKAGE
        openInAppOrInBrowser(context, url, intent)
    }

    private fun openInAppOrInBrowser(context: Context, url: String, intent: Intent) {
        context.safeStartActivity(intent) {
            context.openUrlInBrowser(url)
        }
    }

}