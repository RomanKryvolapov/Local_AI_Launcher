/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit) {
    this.lifecycleScope.launch {
        this@launchWhenResumed.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            block()
        }
    }
}

fun CoroutineScope.launchWithDispatcher(
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        logError(
            "launchWithDispatcher Exception: ${throwable.message}",
            throwable,
            "launchWithDispatcher"
        )
    }
    return launch(Dispatchers.Default + exceptionHandler) {
        block()
    }
}