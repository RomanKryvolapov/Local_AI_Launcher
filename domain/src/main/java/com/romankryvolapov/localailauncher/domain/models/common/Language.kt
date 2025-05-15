/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.models.common

import com.romankryvolapov.localailauncher.common.models.common.TypeEnum

enum class Language(override val type: String) : TypeEnum {
    BULGARIAN("bg"),
    ENGLISH("en"),
}