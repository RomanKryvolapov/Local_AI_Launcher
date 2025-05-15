/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain

import com.romankryvolapov.localailauncher.domain.models.common.ApplicationInfo
import com.romankryvolapov.localailauncher.domain.models.common.ApplicationLanguage
import com.romankryvolapov.localailauncher.domain.models.common.MockResponse
import java.io.File

const val DEBUG_LOGOUT_FROM_PREFERENCES = false
const val DEBUG_PRINT_PREFERENCES_INFO = true
const val DEBUG_MOCK_INTERCEPTOR_ENABLED = true

const val DEBUG_FORCE_REPLACE_ASSETS = false

const val DEFAULT_INACTIVITY_TIMEOUT_MILLISECONDS = 120000L

val defaultApplicationInfo = ApplicationInfo(
    accessToken = "",
    isFirstFun = true,
    refreshToken = "",
    applicationLanguage = ApplicationLanguage.EN,
    selectedModelPosition = 0,
)

val mockResponses = mutableMapOf<String, MockResponse>().apply {
    put(
        key = "",
        value = MockResponse(
            isEnabled = false,
            body = "",
            message = "",
            serverCode = 200,
        )
    )
}

lateinit var externalFilesDirectory: File
lateinit var internalFilesDirectory: File