/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.repository.common

import com.romankryvolapov.localailauncher.domain.models.common.ApplicationInfo

interface PreferencesRepository {

    fun saveApplicationInfo(value: ApplicationInfo)

    fun readApplicationInfo(): ApplicationInfo?

    fun logoutFromPreferences()

}