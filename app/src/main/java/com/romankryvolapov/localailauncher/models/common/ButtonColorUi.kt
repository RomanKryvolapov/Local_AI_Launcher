/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import com.romankryvolapov.localailauncher.common.models.common.TypeEnum

enum class ButtonColorUi(
    override val type: String,
) : TypeEnum {
    BLUE("BLUE"),
    RED("RED"),
    GREEN("GREEN"),
    ORANGE("ORANGE"),
    TRANSPARENT("TRANSPARENT"),
}