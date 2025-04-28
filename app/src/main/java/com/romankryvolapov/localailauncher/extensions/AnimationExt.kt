/**
 * Show or hide view with fade in/out animations.
 * If [useInvisibility] is true, the view end state would be
 * [View.INVISIBLE] otherwise it is [View.GONE].
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListener

fun View.dimView(
    show: Boolean,
    duration: Long = 250,
    endListener: (() -> Unit)? = null,
    delay: Long = 0,
    useInvisibility: Boolean = false
): ViewPropertyAnimatorCompat {
    isEnabled = show
    if (show) alpha = 0f
    val endVisibility = if (useInvisibility) View.INVISIBLE else View.GONE
    val animator = ViewCompat.animate(this)
    animator.alpha(if (show) 1f else 0f)
        .setDuration(duration)
        .setStartDelay(delay)
        .setListener(object : ViewPropertyAnimatorListener {
            override fun onAnimationEnd(view: View) {
                view.visibility = if (show) View.VISIBLE else endVisibility
                alpha = 1f
                endListener?.invoke()
            }

            override fun onAnimationCancel(view: View) {
                view.visibility = endVisibility
            }

            override fun onAnimationStart(view: View) {
                view.visibility = View.VISIBLE
            }
        })
        .withLayer()
        .start()
    return animator
}

fun View.flipUpView(up: Boolean, duration: Long = 250) {
    ViewCompat.animate(this)
        .rotationX(if (up) 0f else 180f)
        .setDuration(duration)
        .withLayer()
        .start()
}

fun View.slideUpView(show: Boolean, duration: Long = 250, endListener: (() -> Unit)? = null) {
    if (tag == show) {
        // already started with same previous value
        return
    }
    tag = show

    isEnabled = show
    if (show) translationY = height.toFloat() * -1
    ViewCompat.animate(this)
        .translationY(if (show) 0f else height.toFloat() * -1)
        .setDuration(duration)
        .setListener(object : ViewPropertyAnimatorListener {
            override fun onAnimationEnd(view: View) {
                view.visibility = if (show) View.VISIBLE else View.GONE
                endListener?.invoke()
            }

            override fun onAnimationCancel(view: View) {
                view.visibility = View.GONE
            }

            override fun onAnimationStart(view: View) {
                if (show) {
                    view.visibility = View.VISIBLE
                }
            }
        })
        .withLayer()
        .start()
}

fun View.expandIfNotVisible() {
    if (visibility != View.VISIBLE) {
        expand()
    }
}

fun View.expand() {
    val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec((parent as View).width,
        View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = measuredHeight

    layoutParams.height = 1
    visibility = View.VISIBLE
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            layoutParams.height = (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }
    }

    a.duration = 250L
    startAnimation(a)
}

fun View.collapseIfVisible() {
    if (visibility == View.VISIBLE) {
        collapse()
    }
}

fun View.collapse() {
    val initialHeight = measuredHeight

    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                visibility = View.GONE
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }
    }

    a.duration = 250L
    startAnimation(a)
}