/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Disable swipe refresh [this] layout when we scroll too far from top.
 * This code will fix the bottom recycler view [rv] glow.
 */
fun SwipeRefreshLayout.fixRecyclerViewGlow(rv: RecyclerView) {
    val layoutManager = rv.layoutManager as LinearLayoutManager
    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
    isEnabled = (firstVisiblePosition == 0)
}