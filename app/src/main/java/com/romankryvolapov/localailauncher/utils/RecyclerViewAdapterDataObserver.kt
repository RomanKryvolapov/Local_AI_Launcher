/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import androidx.recyclerview.widget.RecyclerView
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug

class RecyclerViewAdapterDataObserver : RecyclerView.AdapterDataObserver() {

    companion object {
        private const val TAG = "AdapterDataObserverTag"
    }

    var changeStateListener: (() -> Unit)? = null

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        logDebug("onItemRangeRemoved", TAG)
        changeStateListener?.invoke()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        logDebug("onItemRangeInserted", TAG)
        changeStateListener?.invoke()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        logDebug("onItemRangeChanged", TAG)
        changeStateListener?.invoke()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
        logDebug("onItemRangeMoved", TAG)
        changeStateListener?.invoke()
    }
}