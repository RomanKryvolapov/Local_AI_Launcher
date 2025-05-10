/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import com.romankryvolapov.localailauncher.domain.models.common.ApplicationLanguage
import com.romankryvolapov.localailauncher.domain.repository.common.PreferencesRepository
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.extensions.readOnly
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

class LocalizationManager : KoinComponent {

    companion object {
        private const val TAG = "LocalizationManagerTag"
    }

    private val currentContext: CurrentContext by inject()
    private val preferences: PreferencesRepository by inject()

    @Volatile
    private var inChange = false

    private val _readyLiveData = SingleLiveEvent<Unit>()
    val readyLiveData = _readyLiveData.readOnly()

    fun applyLanguage(
        language: ApplicationLanguage? = null,
    ) {
        if (inChange) return
        inChange = true
        val applicationInfo = preferences.readApplicationInfo()
        if (applicationInfo == null) {
            logError("applyLanguage applicationInfo == null", TAG)
            return
        }
        val newLanguage = if (language != null) {
            preferences.saveApplicationInfo(
                applicationInfo.copy(
                    applicationLanguage = language,
                )
            )
            language
        } else {
            applicationInfo.applicationLanguage
        }
        logDebug("applyLanguage language: ${newLanguage.type}", TAG)
        val languageStr = newLanguage.type
        val locale = Locale(languageStr)
        val configuration = currentContext.get().resources.configuration
        val displayMetrics = currentContext.get().resources.displayMetrics
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        currentContext.get().createConfigurationContext(configuration)
        currentContext.get().resources.updateConfiguration(configuration, displayMetrics)
        _readyLiveData.call()
        inChange = false
    }
}