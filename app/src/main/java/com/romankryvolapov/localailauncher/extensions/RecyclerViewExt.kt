/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.widget.EdgeEffect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.utils.RecyclerViewAdapterDataObserver

/**
 * This method helps to remove the top overscroll effect from the recycler view.
 */

private const val TAG = "RecyclerViewExtTag"

fun RecyclerView.removeTopOverscroll() {
    this.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            return when (direction) {
                DIRECTION_TOP -> object : EdgeEffect(view.context) {
                    // Disable default method behaviour
                    override fun onAbsorb(velocity: Int) {}

                    override fun onPull(deltaDistance: Float, displacement: Float) {}
                }

                else -> super.createEdgeEffect(view, direction)
            }
        }
    }
}

fun RecyclerView.removeAllDecorators() {
    for (i in 0 until itemDecorationCount) {
        removeItemDecorationAt(i)
    }
}

fun RecyclerView.getFirstVisiblePosition(): Int? {
    return (layoutManager as? LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()
}

fun RecyclerView.enableChangeAnimations(isEnable: Boolean) {
    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = isEnable
}

fun RecyclerView.scrollToPositionWithOffset(position: Int) {
    val layoutManager = layoutManager as? LinearLayoutManager
    layoutManager?.scrollToPositionWithOffset(position, height / 2)
    post {
        val viewHolder = findViewHolderForAdapterPosition(position)
        viewHolder?.itemView?.requestFocus()
    }
}

fun RecyclerView.scrollDown(dp: Int) {
    scrollBy(0, context.dpToPx(dp))
}

fun RecyclerView.Adapter<RecyclerView.ViewHolder>.registerChangeStateObserver(
    observer: RecyclerViewAdapterDataObserver,
    changeStateListener: (() -> Unit),
) {
    try {
        registerAdapterDataObserver(observer)
        observer.changeStateListener = changeStateListener
    } catch (e: Exception) {
        logError("registerChangeStateObserver Exception: ${e.message}", e, TAG)
    }
}

fun RecyclerView.Adapter<RecyclerView.ViewHolder>.unregisterChangeStateObserver(
    observer: RecyclerViewAdapterDataObserver,
) {
    try {
        if (hasObservers()) {
            unregisterAdapterDataObserver(observer)
        }
        observer.changeStateListener = null
    } catch (e: Exception) {
        logError("unregisterChangeStateObserver Exception: ${e.message}", e, TAG)
    }
}

