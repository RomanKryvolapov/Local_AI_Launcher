/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.NavActivityDirections
import com.romankryvolapov.localailauncher.domain.DEBUG_LOGOUT_FROM_PREFERENCES
import com.romankryvolapov.localailauncher.domain.DEBUG_PRINT_PREFERENCES_INFO
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import kotlin.system.exitProcess

class SplashViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "SplashViewModelTag"
        private const val PREFERENCES_INFO_TAG = "PreferencesInfoTag"
    }

    override fun onBackPressed() {
        logDebug("onBackPressed", TAG)
        exitProcess(0)
    }

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)
        if (BuildConfig.DEBUG && DEBUG_LOGOUT_FROM_PREFERENCES) {
            preferences.logoutFromPreferences()
            logDebug("logoutFromPreferences", TAG)
        }
        if (BuildConfig.DEBUG && DEBUG_PRINT_PREFERENCES_INFO) {
            logDebug("PRINT_PREFERENCES_INFO", TAG)
            val applicationInfo = preferences.readApplicationInfo()
        }
        navigateInActivity(NavActivityDirections.toAuthFlowFragment())
    }

}