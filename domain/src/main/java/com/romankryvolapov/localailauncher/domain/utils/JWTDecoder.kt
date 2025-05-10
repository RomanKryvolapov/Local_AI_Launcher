/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.utils

import com.romankryvolapov.localailauncher.domain.models.user.UserModel

interface JWTDecoder {

    fun getUser(fromToken: String): UserModel?

}