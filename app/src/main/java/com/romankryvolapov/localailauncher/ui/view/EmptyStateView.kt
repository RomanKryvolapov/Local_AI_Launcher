/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.romankryvolapov.localailauncher.databinding.LayoutEmptyStateBinding
import com.romankryvolapov.localailauncher.extensions.onClickThrottle

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutEmptyStateBinding.inflate(LayoutInflater.from(context), this)

    var reloadClickListener: (() -> Unit)? = null

    init {
        setupControls()
    }

    private fun setupControls() {
        binding.btnEmptyStateViewReload.onClickThrottle { reloadClickListener?.invoke() }
    }

}