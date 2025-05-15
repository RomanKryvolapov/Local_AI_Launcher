/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug

class ComplexGestureRefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    companion object {
        private const val TAG = "ComplexGestureRefreshViewTag"
    }

    var isGestureAllowed = true
        set(value) {
            field = value
            isEnabled = value
            if (!value) {
                setOnRefreshListener(null)
            }
        }

    private val helper = ComplexGestureTouchHelper()

    init {
        setProgressBackgroundColorSchemeResource(R.color.color_0C53B2)
        setColorSchemeResources(R.color.color_0C53B2)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        logDebug("onInterceptTouchEvent isGestureAllowed: $isGestureAllowed", TAG)
        return if (isGestureAllowed && helper.onInterceptTouchEvent(event)) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        logDebug("onTouchEvent isGestureAllowed: $isGestureAllowed", TAG)
        return if (isGestureAllowed) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        logDebug("onNestedScroll isGestureAllowed: $isGestureAllowed", TAG)
        if (isGestureAllowed) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        }
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        logDebug("onNestedFling isGestureAllowed: $isGestureAllowed", TAG)
        return if (isGestureAllowed) {
            super.onNestedFling(target, velocityX, velocityY, consumed)
        } else false
    }

}