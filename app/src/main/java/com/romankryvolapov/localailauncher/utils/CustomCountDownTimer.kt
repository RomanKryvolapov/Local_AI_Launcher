/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.os.CountDownTimer
import java.util.concurrent.TimeUnit

// added extra delay because of different behavior found on API 23, 29 and 30
// more info https://stackoverflow.com/questions/44490897/make-countdown-timer-go-from-10sec-to-1sec/44492812#44492812
open class CustomCountDownTimer(
    duration: Long, unit: TimeUnit
) : CountDownTimer(
    unit.toMillis(duration) + 150L,
    1_000
) {

    var lastTimeTick = unit.toMillis(duration) + 150L
    var finishListener: (() -> Unit)? = null
    var tickListener: ((time: Int) -> Unit)? = null
    var isFinished: Boolean = true
        private set

    override fun onFinish() {
        isFinished = true
        finishListener?.invoke()
    }

    override fun onTick(millisUntilFinished: Long) {
        lastTimeTick = millisUntilFinished
        val seconds = getSeconds()
        if (seconds > 0) {
            tickListener?.invoke(seconds)
        }
    }

    /**
     * Starts a timer or checks that the counting is finished.
     * When we release the timer in removePendingCard and resume next, the timer may be finished
     * already, while the screens was hidden.
     *
     * @before call this method, all listeners should be defined already
     * @param startTimer - true when we need to start a new timer
     */
    fun setupTimer(startTimer: Boolean) {
        tickListener?.invoke(getSeconds())
        if (startTimer) {
            isFinished = false
            start()
        } else {
            if (getSeconds() == 0) {
                isFinished = true
                finishListener?.invoke()
            }
        }
    }

    private fun getSeconds() = lastTimeTick.toInt() / 1000

    /**
     * Release the timer listeners in removePendingCard or onStop methods
     * to prevent some unnecessary exceptions in timer ticks
     */
    fun releaseTimer() {
        cancel()
        tickListener = null
        finishListener = null
    }
}