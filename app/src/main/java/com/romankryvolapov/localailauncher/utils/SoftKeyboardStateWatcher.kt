/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.extensions.pxDimen
import java.lang.ref.WeakReference
import java.util.LinkedList

class SoftKeyboardStateWatcher(
    context: Context,
    layout: View? = null,
    var isSoftKeyboardOpened: Boolean = false
) : ViewTreeObserver.OnGlobalLayoutListener {

    interface SoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeight: Int)
        fun onSoftKeyboardClosed()
    }

    // We take straight activity layout height, but not activity layout.root as
    // later will take in cashAccount soft control buttons and screen decoration
    // causing wrong calculations
    private val parentView = WeakReference(
        (context as AppCompatActivity)
            .findViewById<FrameLayout>(R.id.navigationContainer)
    )

    private val listeners = LinkedList<SoftKeyboardStateListener>()
    private val minKeyboardHeight = context.pxDimen(R.dimen.min_keyboard_height)
    private var lastObservedHeight = 0
    private val activityRect = Rect()
    private val height = parentView.get()?.height ?: 0
    private val childLayout = WeakReference(layout)
    private var activityDiff = 0
    private var statusBarOffset = 0

    fun setStatusBarOffset(height: Int) {
        statusBarOffset = height
    }

    override fun onGlobalLayout() {
        parentView.get()?.removeCallbacks(runnable)
        parentView.get()?.getWindowVisibleDisplayFrame(activityRect)
        activityDiff = height - activityRect.height() - statusBarOffset
        if (lastObservedHeight != activityDiff && activityDiff > 0) isSoftKeyboardOpened = false
        if (!isSoftKeyboardOpened && activityDiff > minKeyboardHeight) { // if more than 100 pixels, its probably a keyboard...
            isSoftKeyboardOpened = true
            parentView.get()?.post(runnable)
        } else if (isSoftKeyboardOpened && activityDiff < minKeyboardHeight) {
            isSoftKeyboardOpened = false
            notifyOnSoftKeyboardClosed()
        }
    }

    private val runnable = Runnable {
        notifyOnSoftKeyboardOpened(activityDiff)
    }

    fun addSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.add(listener)
        parentView.get()?.viewTreeObserver?.addOnGlobalLayoutListener(this)
    }

    /**
     * Its important to remove that listener.
     */
    fun removeSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        if (parentView.get()?.viewTreeObserver?.isAlive == true) {
            parentView.get()?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
        listeners.remove(listener)
    }

    private fun notifyOnSoftKeyboardOpened(keyboardHeight: Int) {
        lastObservedHeight = keyboardHeight
        //if root view and current view are not same   (eg on screens with tabs) we set offset
        val child = childLayout.get()
        val offset =
            child?.let {
                height - it.height
            } ?: 0
        for (listener in listeners) {
            listener.onSoftKeyboardOpened(keyboardHeight - offset)
        }
    }

    private fun notifyOnSoftKeyboardClosed() {
        for (listener in listeners) {
            listener.onSoftKeyboardClosed()
        }
    }
}