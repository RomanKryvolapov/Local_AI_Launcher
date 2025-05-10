/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.activity

import android.content.Intent
import androidx.annotation.CallSuper
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.models.common.StartDestination
import com.romankryvolapov.localailauncher.ui.BaseViewModel

class MainViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "MainViewModelTag"
    }
    fun getStartDestination(intent: Intent): StartDestination {
        return StartDestination(R.id.startFlowFragment)
    }

    @CallSuper
    override fun onFirstAttach() {
        super.onFirstAttach()
        logDebug("onFirstAttach", TAG)

    }

    fun onResume() {
        inactivityTimer.activityOnResume()
    }

    fun onPause() {
        inactivityTimer.activityOnPause()
    }

    fun onDestroy() {
        inactivityTimer.activityOnDestroy()
    }

    fun dispatchTouchEvent() {
        inactivityTimer.dispatchTouchEvent()
    }

    override fun onBackPressed() {
        logDebug("onBackPressed", TAG)
        if (!findActivityNavController().popBackStack()) {
            closeActivity()
        }
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
    }

}