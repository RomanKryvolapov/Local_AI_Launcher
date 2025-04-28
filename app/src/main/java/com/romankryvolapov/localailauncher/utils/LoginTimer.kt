/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface LoginTimer {

    val lockStatusLiveData: LiveData<Boolean>

    fun setTimerCoroutineScope(viewModelScope: CoroutineScope)

    fun activityOnResume()

    fun fragmentOnResume(isLoginTimerEnabled: Boolean)

    fun dispatchTouchEvent()

    fun activityOnPause()

    fun activityOnDestroy()
}

class LoginTimerImpl : LoginTimer {

    companion object {
        private const val TAG = "LoginTimerTag"
        private const val IN_APP_TIMEOUT_MILLISECONDS = 300000L
        private const val OUT_APP_TIMEOUT_MILLISECONDS = 120000L
    }

    private val _lockStatusLiveData = MutableLiveData(false)
    override val lockStatusLiveData = _lockStatusLiveData.readOnly()

    private var timerJob: Job? = null

    private var isLoginTimerEnabled = false

    private var backgroundStartTime: Long = 0

    private var viewModelScope: CoroutineScope? = null

    override fun setTimerCoroutineScope(viewModelScope: CoroutineScope) {
        logDebug("setTimerCoroutineScope", TAG)
        this.viewModelScope = viewModelScope
    }

    override fun activityOnResume() {
        if (isLoginTimerEnabled &&
            backgroundStartTime != 0L &&
            System.currentTimeMillis() - backgroundStartTime > OUT_APP_TIMEOUT_MILLISECONDS
        ) {
            logDebug(
                "activityOnResume out app timer timeout, backgroundStartTime: $backgroundStartTime",
                TAG
            )
            _lockStatusLiveData.setValueOnMainThread(true)
        } else {
            logDebug(
                "activityOnResume but not out app timer timeout, isLoginTimerEnabled: $isLoginTimerEnabled backgroundStartTime: $backgroundStartTime",
                TAG
            )
        }
        backgroundStartTime = 0L
    }

    override fun fragmentOnResume(isLoginTimerEnabled: Boolean) {
        this.isLoginTimerEnabled = isLoginTimerEnabled
        if (isLoginTimerEnabled) {
            logDebug("fragmentOnResume start in app timer", TAG)
            startInAppTimer()
        } else {
            logDebug("fragmentOnResume disable in app timer", TAG)
            timerJob?.cancel()
            backgroundStartTime = 0L
        }
    }

    override fun dispatchTouchEvent() {
        if (isLoginTimerEnabled) {
            logDebug("dispatchTouchEvent in app timer", TAG)
            startInAppTimer()
        }
    }

    private fun startInAppTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope?.launch(Dispatchers.IO) {
            delay(IN_APP_TIMEOUT_MILLISECONDS)
            logDebug("in app timer timeout", TAG)
            _lockStatusLiveData.setValueOnMainThread(true)
        }
    }

    override fun activityOnPause() {
        if (isLoginTimerEnabled) {
            logDebug("activityOnPause start out app timer", TAG)
            backgroundStartTime = System.currentTimeMillis()
            timerJob?.cancel()
        } else {
            logDebug("activityOnPause but not start out app timer", TAG)
        }
    }

    override fun activityOnDestroy() {
        logDebug("activityOnDestroy", TAG)
        isLoginTimerEnabled = false
        timerJob?.cancel()
        backgroundStartTime = 0L
    }

}