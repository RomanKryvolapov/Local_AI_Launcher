/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.core.text.isDigitsOnly
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError

fun isValidLNCH(lnch: String): Boolean {
    if (!lnch.isDigitsOnly()) return false
    if (!isValidLength(lnch)) return false
    val lastDigit = calculateLastDigit(lnch)
    return isValidLastDigit(lastDigit, lnch)
}

private fun calculateLastDigit(lnch: String): Int {
    val weights = listOf(21, 19, 17, 13, 11, 9, 7, 3, 1)
    var lastDigit =
        weights.indices.sumOf { weights[it] * (lnch[it].toString().toIntOrNull() ?: 0) }
    lastDigit %= 10
    return lastDigit
}

private fun isValidLastDigit(lastDigit: Int, egnOrLnch: String): Boolean {
    return lastDigit == (egnOrLnch.substring(9, 10).toIntOrNull() ?: 0)
}

private fun isValidLength(egnOrLnch: String): Boolean {
    return egnOrLnch.length == 10
}

fun isValidEGN(
    personalIdentificationNumber: String,
): Boolean {
    return personalIdentificationNumber.length == 10
            && checkValidationCode(personalIdentificationNumber)
            && checkValidDate(personalIdentificationNumber)
}

private fun checkValidationCode(personalIdentificationNumber: String): Boolean {
    return try {
        val weight = intArrayOf(2, 4, 8, 5, 10, 9, 7, 3, 6)
        val mySum = weight.indices.sumBy {
            weight[it] * personalIdentificationNumber[it].toString().toInt()
        }
        personalIdentificationNumber.last().toString() == (mySum % 11).toString().last()
            .toString()
    } catch (e: NumberFormatException) {
        logError("checkValidationCode Exception: ${e.message}", e, "checkValidationCode")
        false
    }
}

@SuppressLint("SimpleDateFormat")
private fun checkValidDate(personalIdentificationNumber: String): Boolean {
    try {
        val year = personalIdentificationNumber.substring(0, 2).toInt()
        val month = personalIdentificationNumber.substring(2, 4).toInt()
        val day = personalIdentificationNumber.substring(4, 6).toInt()
        val adjustedYear: Int = when {
            month >= 40 -> year + 2000
            month >= 20 -> year + 1800
            else -> year + 1900
        }
        val dateString = "$adjustedYear-$month-$day"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.isLenient = false
        dateFormat.parse(dateString)
        return true
    } catch (e: Exception) {
        logError("checkValidDate Exception: ${e.message}", e, "checkValidationCode")
        return false
    }
}