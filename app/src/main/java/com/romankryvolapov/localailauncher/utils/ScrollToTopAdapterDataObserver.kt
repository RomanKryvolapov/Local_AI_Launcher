/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import androidx.recyclerview.widget.RecyclerView

class ScrollToTopAdapterDataObserver() : RecyclerView.AdapterDataObserver() {

    var isEnabled: Boolean = true
    var recyclerView: RecyclerView? = null
    var onScrolledListener: (() -> Unit)? = null

    override fun onChanged() {
        if (isEnabled) {
            recyclerView?.scrollToPosition(0)
            onScrolledListener?.invoke()
        }
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        if (isEnabled) {
            recyclerView?.scrollToPosition(0)
            onScrolledListener?.invoke()
        }
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        if (isEnabled) {
            recyclerView?.scrollToPosition(0)
            onScrolledListener?.invoke()
        }
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (isEnabled) {
            recyclerView?.scrollToPosition(0)
            onScrolledListener?.invoke()
        }
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        if (isEnabled) {
            recyclerView?.scrollToPosition(0)
            onScrolledListener?.invoke()
        }
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        if (isEnabled) {
            recyclerView?.scrollToPosition(0)
            onScrolledListener?.invoke()
        }
    }
}