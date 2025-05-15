/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.romankryvolapov.localailauncher.domain.DEFAULT_INACTIVITY_TIMEOUT_MILLISECONDS
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

interface InactivityTimer {

    val lockStatusLiveData: LiveData<Boolean>

    fun setTimerCoroutineScope(viewModelScope: CoroutineScope)

    fun activityOnResume()

    fun fragmentOnResume(isActivityTimerEnabled: Boolean)

    fun dispatchTouchEvent()

    fun activityOnPause()

    fun activityOnDestroy()

    fun setNewInactivityTimeout(timeoutInMilliseconds: Long)
}

final class InactivityTimerImpl : InactivityTimer {

    companion object {
        private const val TAG = "InactivityTimerImpl"
    }

    private val _lockStatusLiveData = MutableLiveData(false)
    override val lockStatusLiveData = _lockStatusLiveData.readOnly()

    private var activityTimerJob: Job? = null

    private var isActivityTimerEnabled = false

    private var backgroundTime = 0L

    private var viewModelScope: CoroutineScope? = null

    private var inactivityTimeout = DEFAULT_INACTIVITY_TIMEOUT_MILLISECONDS

    override fun activityOnResume() {
        if (backgroundTime != 0L && System.currentTimeMillis() - backgroundTime > inactivityTimeout) {
            logDebug(
                "activityOnResume out app timer timeout, backgroundTime: $backgroundTime",
                TAG
            )
            _lockStatusLiveData.setValueOnMainThread(true)
        } else {
            logDebug(
                "activityOnResume but not out app timer timeout, isActivityTimerEnabled: $isActivityTimerEnabled backgroundTime: $backgroundTime",
                TAG
            )
        }
        backgroundTime = 0L
    }

    override fun fragmentOnResume(isActivityTimerEnabled: Boolean) {
        this.isActivityTimerEnabled = isActivityTimerEnabled
        if (isActivityTimerEnabled) {
            logDebug("fragmentOnResume start activity timer", TAG)
            startActivityTimer()
        } else {
            logDebug("fragmentOnResume disable activity timer", TAG)
            activityTimerJob?.cancel()
            backgroundTime = 0L
        }
    }

    override fun dispatchTouchEvent() {
        if (isActivityTimerEnabled.not()) return
        logDebug("dispatchTouchEvent activity timer", TAG)
        startActivityTimer()
    }

    override fun activityOnPause() {
        if (isActivityTimerEnabled) {
            logDebug("activityOnPause", TAG)
            backgroundTime = System.currentTimeMillis()
            activityTimerJob?.cancel()
        } else {
            logDebug("activityOnPause", TAG)
        }
    }

    override fun activityOnDestroy() {
        logDebug("activityOnDestroy", TAG)
        isActivityTimerEnabled = false
        activityTimerJob?.cancel()
        backgroundTime = 0L
    }

    override fun setNewInactivityTimeout(timeoutInMilliseconds: Long) {
        inactivityTimeout = timeoutInMilliseconds
        startActivityTimer()
    }

    override fun setTimerCoroutineScope(viewModelScope: CoroutineScope) {
        logDebug("setTimerCoroutineScope", TAG)
        this.viewModelScope = viewModelScope
    }

    private fun startActivityTimer() {
        activityTimerJob?.cancel()
        activityTimerJob = viewModelScope?.launch(Dispatchers.IO) {
            runCatchingCancelable {
                delay(inactivityTimeout)
                logDebug("activity timer timeout", TAG)
                _lockStatusLiveData.setValueOnMainThread(true)
            }
        }
    }

    private inline fun <R> runCatchingCancelable(block: () -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}