/**
 * Generate the app full name for backend in format:
 * [App name]/[App version] ([Device OS name]/[Device OS version])
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.getAppDeviceOsFullNameForAgent(): String {
    val packageInfo = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0)
    } else {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    }
    val appVersion = packageInfo.versionName
    val androidNameVersion = getAndroidVersion(Build.VERSION.SDK_INT)
    return "name/$appVersion ($androidNameVersion)"
}

fun getAndroidVersion(sdk: Int): String {
    return when (sdk) {
        21 -> "Android/5.0"
        22 -> "Android/5.1"
        23 -> "Android/6.0"
        24 -> "Android/7.0"
        25 -> "Android/7.1.1"
        26 -> "Android/8.0"
        27 -> "Android/8.1"
        28 -> "Android/9.0"
        29 -> "Android/10.0"
        30 -> "Android/11.0"
        31 -> "Android/12.0"
        32 -> "Android/12.1"
        33 -> "Android/13.0"
        else -> "Android/Unsupported"
    }
}