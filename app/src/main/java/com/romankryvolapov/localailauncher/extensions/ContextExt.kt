/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File

@SuppressLint("HardwareIds")
fun Context.getDeviceId(): String {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}

/**
 * The backward compatible version of [PackageManager.queryIntentActivities] method.
 */
fun PackageManager.queryIntentActivitiesCompat(intent: Intent): List<ResolveInfo> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        queryIntentActivities(
            intent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
        )
    } else {
        @Suppress("DEPRECATION")
        queryIntentActivities(intent, 0)
    }
}

/**
 * Starts the activity from [intent] if it is possible,
 * otherwise [failCallback] is triggered.
 */
fun Context.safeStartActivity(intent: Intent, failCallback: (() -> Unit)? = null) {
    if (packageManager.queryIntentActivitiesCompat(intent).isNotEmpty()) {
        ContextCompat.startActivity(this, intent, null)
    } else {
        failCallback?.invoke()
    }
}

fun Context.callPhoneNumber(phoneNumber: String) {
    val phoneNumberUri = "tel:$phoneNumber"
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumberUri))
    safeStartActivity(intent)
}

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    safeStartActivity(intent)
}

fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_SETTINGS)
    safeStartActivity(intent)
}

fun Context.openApplicationSettings(fragment: Fragment) {
    val packageName = this.packageName
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, fragment.tag)
    }
    val list = packageManager.queryIntentActivitiesCompat(intent)
    if (list.isNotEmpty()) {
        fragment.startActivity(intent)
    }
}

fun Context.openLocationSettings() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    safeStartActivity(intent)
}

fun Context.openFile(file: File) {
    val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
    val mime = contentResolver.getType(uri)

    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.data = uri
    intent.setDataAndType(uri, mime)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    safeStartActivity(intent)
}

fun colorStateListOf(vararg statesWithColorRes: Pair<IntArray, Int>): ColorStateList {
    val (states, colors) = statesWithColorRes.unzip()
    return ColorStateList(states.toTypedArray(), colors.toIntArray())
}

fun Context.colorStateListOf(@ColorRes colorRes: Int): ColorStateList {
    return ColorStateList.valueOf(color(colorRes))
}

fun Context.openGoogleMapNavigation(latitude: Double, longitude: Double) {
    Intent(Intent.ACTION_VIEW, Uri.parse("$latitude,$longitude")).apply {
        setPackage("com.google.android.apps.maps")
        resolveActivity(packageManager)?.let {
            startActivity(this)
        }
    }
}