/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.repository.common

import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import com.romankryvolapov.localailauncher.data.BuildConfig.PROPERTY_KEY_FIREBASE_TOKEN
import com.romankryvolapov.localailauncher.data.BuildConfig.PROPERTY_KEY_PIN_CODE
import com.romankryvolapov.localailauncher.domain.models.common.ApplicationInfo
import com.romankryvolapov.localailauncher.domain.repository.common.PreferencesRepository
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.google.gson.Gson
import com.romankryvolapov.localailauncher.domain.defaultApplicationInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class PreferencesRepositoryImpl : PreferencesRepository, KoinComponent {

    companion object {
        private const val TAG = "PreferencesRepositoryTag"
    }

    private val preferences: EncryptedSharedPreferences by inject()

    override fun saveApplicationInfo(value: ApplicationInfo) {
        logDebug("savePinCode value: $value", TAG)
        saveObject(value, PROPERTY_KEY_PIN_CODE)
    }

    override fun readApplicationInfo(): ApplicationInfo {
        val value = readObject(ApplicationInfo::class.java, PROPERTY_KEY_PIN_CODE)
        logDebug("readPinCode value: $value", TAG)
        return value ?: defaultApplicationInfo
    }

    override fun logoutFromPreferences() {
        logDebug("logoutFromPreferences", TAG)
        preferences.edit {
            remove(PROPERTY_KEY_PIN_CODE)
            remove(PROPERTY_KEY_FIREBASE_TOKEN)
        }
    }

    private fun saveObject(dataObject: Any, key: String) {
        val objectJson = Gson().toJson(dataObject)
        preferences.edit().putString(key, objectJson).apply()
    }

    private fun <T> readObject(baseClass: Class<T>, key: String): T? {
        val dataObject: String? = preferences.getString(key, "")
        return try {
            Gson().fromJson(dataObject, baseClass)
        } catch (e: Exception) {
            logError("readObject Exception: ${e.message}", e, TAG)
            null
        }
    }

}