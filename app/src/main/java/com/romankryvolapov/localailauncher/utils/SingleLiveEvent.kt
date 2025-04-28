/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T>() : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    constructor(value: T) : this() {
        setValue(value)
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.d(
                "SingleLiveEvent",
                "Multiple observers registered but only one will be notified of changes."
            )
        }
        super.observe(owner) { value ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(value)
            }
        }
    }

    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    fun callOnMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            value = null
        } else {
            Handler(Looper.getMainLooper()).post {
                value = null
            }
        }
    }
}