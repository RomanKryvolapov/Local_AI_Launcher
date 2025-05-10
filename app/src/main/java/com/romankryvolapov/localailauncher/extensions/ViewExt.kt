/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.allViews
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.romankryvolapov.localailauncher.R
import kotlin.math.abs
import kotlin.math.round

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun isDark(@ColorInt color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5
}

fun Context.dpToPx(valueInDp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        valueInDp,
        resources.displayMetrics
    )
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.backgroundColor(@ColorRes colorRes: Int) {
    setBackgroundColor(context.color(colorRes))
}

fun View.setAlphaByOffset(
    incomingOffset: Int,
    maxOffset: Int,
    fraction: Float = 0.2f
) {
    val offset = abs(incomingOffset)
    val minOffset = maxOffset * (1f - fraction)
    alpha = if (offset < minOffset) {
        0f
    } else if (offset >= maxOffset) {
        1f
    } else {
        (offset - minOffset) / (maxOffset - minOffset)
    }
}

/**
 * Get action bar height from device theme or just take the backup
 * height when the device has no actionBarSize attribute.
 */
fun View?.fetchActionBarHeight(): Int {
    val tv = TypedValue()
    var actionBarHeight = 0
    if (this != null) {
        actionBarHeight = if (context.theme.resolveAttribute(
                android.R.attr.actionBarSize, tv,
                true
            ) && !isInEditMode
        ) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        } else {
            30
//            context.pxDimen(R.dimen.unused_action_bar_size)
        }
    }
    return actionBarHeight
}

/**
 * Ignore the fast series of clicks to prevent multiple action calls.
 *
 * @param call - a click listener
 * @return disposable of rx click method. Should be disposed with view destructor
 */
inline fun View.onClickThrottle(delay: Long = 500L, crossinline call: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        var time = 0L
        override fun onClick(v: View?) {
            val newTime = System.currentTimeMillis()
            if (newTime - time > delay) {
                call.invoke()
                time = newTime
            }
        }
    })
}

inline fun View.onClick(crossinline call: () -> Unit) {
    setOnClickListener {
        call.invoke()
    }
}

fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                fallback = view
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback
}

fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
    val alpha = round(Color.alpha(color) * factor).toInt()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}

fun Rect.copy(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null): Rect {
    return Rect(
        left ?: this.left,
        top ?: this.top,
        right ?: this.right,
        bottom ?: this.bottom
    )
}

/**
 * The method changes the view elevation using the
 * scroll offset [incomingOffset] which could be the scroll offset of app bar,
 * recycler view or nested scroll, etc. The [maxOffset] could be set to restraint
 * the top barrier of elevation changing, basically defines the speed of animation.
 * The [minOffset] could be set to restrain the bottom barrier, where the animation
 * starts.
 *
 * With the scrolling, the elevation of the view will be slowly
 * increased to the maximum. The start elevation should be 0.
 */
fun View.bindElevationToOffset(incomingOffset: Int, maxOffset: Int = 80, minOffset: Int = 0) {
    val maxElevation = 10
    //    context.pxDimen(R.dimen.default_elevation)
    val totalOffsetAvailable = minOffset + maxOffset
    val offset = abs(incomingOffset)
    when {
        offset <= minOffset -> {
            if (elevation > 0f) elevation = 0f
        }

        offset >= totalOffsetAvailable -> {
            if (elevation < maxElevation) elevation = maxElevation.toFloat()
        }

        else -> {
            elevation = offset * maxElevation / totalOffsetAvailable.toFloat()
        }
    }
}

/**
 * The method changes the view alpha using the
 * scroll offset [incomingOffset] which could be the scroll offset of app bar,
 * recycler view or nested scroll, etc. The [maxOffset] could be set to restraint
 * the top barrier of alpha changing, basically defines the speed of animation.
 * The [minOffset] could be set to restrain the bottom barrier, where the animation
 * starts.
 *
 * With the scrolling, the alpha of the view will be slowly
 * increased to the maximum. The start alpha should be 0.
 */
fun View.bindAlphaToOffset(incomingOffset: Int, maxOffset: Int = 80, minOffset: Int = 0) {
    val totalOffsetAvailable = minOffset + maxOffset
    val offset = abs(incomingOffset)
    when {
        offset <= minOffset -> {
            if (alpha > 0f) alpha = 0f
        }

        offset >= totalOffsetAvailable -> {
            if (alpha < 1f) alpha = 1f
        }

        else -> {
            alpha = offset / totalOffsetAvailable.toFloat()
        }
    }
}

/**
 * The method changes the view visibility with fade animation using the
 * scroll offset [incomingOffset] which could be the scroll offset of app bar,
 * recycler view or nested scroll, etc. The [maxOffset] could be set to restraint
 * the top barrier to trigger the animation.
 * The start visibility should be INVISIBLE.
 */
fun View.bindAnimatedVisibilityToOffset(incomingOffset: Int, maxOffset: Int = 80) {
    val offset = abs(incomingOffset)
    val show = when {
        offset >= maxOffset && isInvisible -> true
        offset < maxOffset && isVisible -> false
        else -> null
    }

    // Start animation if we need
    show?.let {
        dimView(
            show = it,
            duration = resources.getInteger(R.integer.short_duration).toLong(),
            useInvisibility = true
        )
    }
}

/**
 * The method returns the parsed color using the
 * scroll offset [incomingOffset]
 *
 * @param incomingOffset - the scroll offset of app bar,
 *                         recycler view or nested scroll
 */
@ColorInt
fun getColorToOffset(
    context: Context,
    incomingOffset: Int,
    maxOffset: Int = 80,
    @ColorRes expandedColor: Int,
    @ColorRes collapsedColor: Int
): Int {
    val offset = abs(incomingOffset)
    val fraction: Float = if (offset >= maxOffset) {
        1f
    } else {
        offset / maxOffset.toFloat()
    }

    return ArgbEvaluatorCompat.getInstance().evaluate(
        fraction,
        context.color(expandedColor),
        context.color(collapsedColor)
    )
}

/**
 * Method set view background color from resource
 */
fun View.setBackgroundColorResource(@ColorRes resource: Int) {
    setBackgroundColor(context.color(resource))
}

fun View.setBackgroundDrawableResource(@DrawableRes resource: Int) {
    background = context.drawable(resource)
}

fun RadioGroup.applyColorStateList(colorStateList: ColorStateList) {
    allViews
        .map { it as? RadioButton }
        .forEach { it?.buttonTintList = colorStateList }
}