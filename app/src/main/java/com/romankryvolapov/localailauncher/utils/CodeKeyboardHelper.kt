/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.os.Handler
import android.os.Looper
import com.romankryvolapov.localailauncher.models.common.InputLockState
import com.romankryvolapov.localailauncher.models.common.LockState
import java.util.concurrent.TimeUnit

@JvmInline
value class CodeKeyboardHelper(
    private val inputLockState: (InputLockState) -> Unit,
) {

    fun lockKeyboardForTimeout(
        durationMilliseconds: Long,
        showTimer: Boolean = false,
    ) {
        lockKeyboard(
            durationMilliseconds = durationMilliseconds
        )
        if (showTimer) {
            startTimerAndShowTime(
                durationMilliseconds = durationMilliseconds
            )
        } else {
            startTimer(
                durationMilliseconds = durationMilliseconds
            )
        }
    }

    private fun lockKeyboard(durationMilliseconds: Long) {
        inputLockState(
            InputLockState(
                state = LockState.LOCKED,
                timeLeftMilliseconds = durationMilliseconds
            )
        )
    }

    private fun startTimer(durationMilliseconds: Long) {
        val unlockKeyboard = Runnable {
            inputLockState(
                InputLockState(
                    state = LockState.UNLOCKED,
                    timeLeftMilliseconds = null,
                )
            )
        }
        Handler(Looper.getMainLooper()).postDelayed(unlockKeyboard, durationMilliseconds)
    }

    private fun startTimerAndShowTime(durationMilliseconds: Long) {
        CustomCountDownTimer(durationMilliseconds, TimeUnit.MILLISECONDS).apply {
            tickListener = { seconds ->
                inputLockState(
                    InputLockState(
                        state = LockState.LOCKED,
                        timeLeftMilliseconds =
                        TimeUnit.SECONDS.toMillis(seconds.toLong())
                    )
                )
            }
            finishListener = {
                inputLockState(
                    InputLockState(
                        state = LockState.UNLOCKED,
                        timeLeftMilliseconds = null,
                    )
                )
            }
            setupTimer(true)
        }
    }
}