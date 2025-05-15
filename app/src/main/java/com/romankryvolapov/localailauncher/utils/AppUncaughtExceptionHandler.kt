/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError

class AppUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "AppUncaughtExceptionHandlerTag"
    }

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        logError("${exception.message}", exception, TAG)
    }

}