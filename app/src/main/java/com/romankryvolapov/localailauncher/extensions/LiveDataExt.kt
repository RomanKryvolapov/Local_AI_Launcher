/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.readOnly(): LiveData<T> = this

@MainThread
fun MutableLiveData<Unit>.call() {
    this.value = Unit
}

fun <T> MutableLiveData<T>.setValueOnMainThread(value: T?) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        this.value = value
    } else {
        Handler(Looper.getMainLooper()).post {
            this.value = value
        }
    }
}