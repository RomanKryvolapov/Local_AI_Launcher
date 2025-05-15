/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.LayoutLoaderBinding
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.setBackgroundColorResource

class LoaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "LoaderViewTag"
    }

    private val binding = LayoutLoaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundColorResource(R.color.color_transparent)
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.LoaderView) {
            getString(R.styleable.LoaderView_loader_view_message)?.let {
                logDebug("tvMessage text: $it", TAG)
                binding.tvMessage.text = it
            }
        }
    }

    fun setMessage(message: String) {
        binding.tvMessage.text = message
    }

}