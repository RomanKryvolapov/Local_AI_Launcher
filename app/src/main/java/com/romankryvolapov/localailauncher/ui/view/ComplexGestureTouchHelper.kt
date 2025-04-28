/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.graphics.Point
import android.view.MotionEvent
import kotlin.math.abs

class ComplexGestureTouchHelper {

    private val dragThreshold = 100
    private var down = Point(0, 0)

    /**
     * Intercept touch and decide of the fate of the event.
     * When the user move finger up or down, only the touches with
     * appropriate conditions will be thrown to coordinator handler,
     * the other touches will be handled by View Pager.
     *
     * Condition: the vertical uriPath may be at least 100px long
     * and it may be two times longer than the horizontal uriPath.
     *
     * @param event - the incoming touch event
     */
    fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                down.set(event.rawX.toInt(), event.rawY.toInt())
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs(event.rawX.toInt() - down.x)
                val dy = abs(event.rawY.toInt() - down.y)

                if (dy < dragThreshold || dy < dx * 2) {
                    return false
                }
            }
        }
        return true
    }

}