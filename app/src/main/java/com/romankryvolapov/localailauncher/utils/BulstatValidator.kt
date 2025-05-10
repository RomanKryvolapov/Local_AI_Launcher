/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

private val FIRST_SUM_9DIGIT_WEIGHTS = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
private val SECOND_SUM_9DIGIT_WEIGHTS = intArrayOf(3, 4, 5, 6, 7, 8, 9, 10)
private val FIRST_SUM_13DIGIT_WEIGHTS = intArrayOf(2, 7, 3, 5)
private val SECOND_SUM_13DIGIT_WEIGHTS = intArrayOf(4, 9, 5, 7)

fun isValidBulstat(eik: String): Boolean {
    return when (eik.length) {
        9 -> safelyCalculateChecksumForNineDigitsEIK(eik)
        13 -> safelyCalculateChecksumForThirteenDigitsEIK(eik)
        else -> false
    }
}

private fun safelyCalculateChecksumForNineDigitsEIK(eik: String): Boolean {
    return try {
        val digits = checkInput(eik, 9)
        val ninthDigit = calculateNinthDigitInEIK(digits)
        ninthDigit == digits[8]
    } catch (e: IllegalArgumentException) {
        false
    }
}

private fun safelyCalculateChecksumForThirteenDigitsEIK(eik: String): Boolean {
    return try {
        val digits = checkInput(eik, 13)
        val thirteenthDigit = calculateThirteenthDigitInEIK(digits)
        thirteenthDigit == digits[12]
    } catch (e: IllegalArgumentException) {
        false
    }
}

private fun calculateNinthDigitInEIK(digits: IntArray): Int {
    var sum = 0
    for (i in 0 until 8) {
        sum += digits[i] * FIRST_SUM_9DIGIT_WEIGHTS[i]
    }
    val remainder = sum % 11
    if (remainder != 10) {
        return remainder
    }
    var secondSum = 0
    for (i in 0 until 8) {
        secondSum += digits[i] * SECOND_SUM_9DIGIT_WEIGHTS[i]
    }
    val secondRem = secondSum % 11
    return if (secondRem != 10) secondRem else 0
}

private fun calculateThirteenthDigitInEIK(digits: IntArray): Int {
    val ninthDigit = calculateNinthDigitInEIK(digits)
    if (ninthDigit != digits[8]) {
        throw IllegalArgumentException("Incorrect 9th digit in EIK-13.")
    }
    var sum = 0
    for ((i, j) in (8 until digits.size).withIndex()) {
        sum += digits[i] * FIRST_SUM_13DIGIT_WEIGHTS[j]
    }
    val remainder = sum % 11
    if (remainder != 10) {
        return remainder
    }
    var secondSum = 0
    for ((i, j) in (8 until digits.size).withIndex()) {
        secondSum += digits[i] * SECOND_SUM_13DIGIT_WEIGHTS[j]
    }
    val secondRem = secondSum % 11
    return if (secondRem != 10) secondRem else 0
}

private fun checkInput(eik: String, eikLength: Int): IntArray {
    require(eik.length == eikLength) {
        "Incorrect count of digits in EIK: ${eik.length} != $eikLength"
    }
    return eik.map { char ->
        char.digitToIntOrNull() ?: throw IllegalArgumentException("Incorrect input character. Only digits are allowed.")
    }.toIntArray()
}