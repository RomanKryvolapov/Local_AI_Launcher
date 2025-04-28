/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.models.common

enum class ErrorStatus(val timeoutMillis: Long, val text: String) {
    NO_TIMEOUT(0L, ""),
    TIMEOUT_30_SECONDS(30_000L, "30 seconds"),
    TIMEOUT_5_MINUTES(300_000L, "5 minutes"),
    TIMEOUT_1_HOUR(3_600_000L, "1 hour"),
    TIMEOUT_24_HOUR(86_400_000L, "24 hour"),
}