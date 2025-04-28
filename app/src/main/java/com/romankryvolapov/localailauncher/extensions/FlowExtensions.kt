/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

fun <T> Flow<T>.launchInScope(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launch {
        this@launchInScope.flowOn(Dispatchers.Main)
            .collect()
    }
}

fun <T> Flow<T>.launchInScope(viewModelScope: CoroutineScope) {
    viewModelScope.launch {
        this@launchInScope.flowOn(Dispatchers.IO)
            .collect()
    }
}

fun <T> Flow<T>.launchInJob(lifecycleScope: LifecycleCoroutineScope): Job {
    return lifecycleScope.launch {
        this@launchInJob.flowOn(Dispatchers.Main)
            .collect()
    }
}

fun <T> Flow<T>.launchInJob(viewModelScope: CoroutineScope): Job {
    return viewModelScope.launch {
        this@launchInJob.flowOn(Dispatchers.IO)
            .collect()
    }
}