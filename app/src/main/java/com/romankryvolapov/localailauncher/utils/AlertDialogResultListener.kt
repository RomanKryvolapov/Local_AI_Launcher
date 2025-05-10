/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import com.romankryvolapov.localailauncher.models.common.AlertDialogResult

fun interface AlertDialogResultListener {

    fun onAlertDialogResultReady(result: AlertDialogResult)

}