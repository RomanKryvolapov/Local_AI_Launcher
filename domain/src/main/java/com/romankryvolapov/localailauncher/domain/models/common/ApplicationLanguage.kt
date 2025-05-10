/**
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.models.common

import com.romankryvolapov.localailauncher.domain.models.base.TypeEnum

enum class ApplicationLanguage(
    override val type: String,
    val nameString: String
) : TypeEnum {
    EN("en", "English"),
    BG("bg", "Български"),
}