/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.LayoutErrorBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.onClickThrottle

class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ErrorViewTag"
    }

    private val binding = LayoutErrorBinding.inflate(LayoutInflater.from(context), this)

    var actionOneClickListener: (() -> Unit)? = null
    var actionTwoClickListener: (() -> Unit)? = null

    init {
        setupAttributes(attrs)
        setupControls()
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.ErrorView) {
            getBoolean(R.styleable.ErrorView_error_view_show_title, true).let {
                logDebug("tvErrorViewTitle isVisible: $it", TAG)
                binding.tvErrorViewTitle.isVisible = it
            }
            getBoolean(R.styleable.ErrorView_error_view_show_description, true).let {
                logDebug("tvErrorViewDescription isVisible: $it", TAG)
                binding.tvErrorViewDescription.isVisible = it
            }
            getBoolean(R.styleable.ErrorView_error_view_show_button_one, true).let {
                logDebug("error_view_show_button_one isVisible: $it", TAG)
                binding.btnErrorActionOne.isVisible = it
            }
            getBoolean(R.styleable.ErrorView_error_view_show_button_two, false).let {
                logDebug("error_view_show_button_two isVisible: $it", TAG)
                binding.btnErrorActionTwo.isVisible = it
            }
            getString(R.styleable.ErrorView_error_view_title)?.let {
                logDebug("tvErrorViewTitle text: $it", TAG)
                binding.tvErrorViewTitle.text = it
            }
            getString(R.styleable.ErrorView_error_view_description)?.let {
                logDebug("tvErrorViewDescription text: $it", TAG)
                binding.tvErrorViewDescription.text = it
            }
            getString(R.styleable.ErrorView_error_view_button_one_title)?.let {
                logDebug("btnErrorActionOne text: $it", TAG)
                binding.btnErrorActionOne.text = it
            }
            getString(R.styleable.ErrorView_error_view_button_two_title)?.let {
                logDebug("btnErrorActionTwo text: $it", TAG)
                binding.btnErrorActionTwo.text = it
            }
            getDrawable(R.styleable.ErrorView_error_view_icon)?.let {
                binding.ivErrorIcon.setImageDrawable(it)
            }
        }
    }

    private fun setupControls() {
        binding.btnErrorActionOne.onClickThrottle {
            logDebug("btnErrorActionOne onClickThrottle", TAG)
            actionOneClickListener?.invoke()
        }
        binding.btnErrorActionTwo.onClickThrottle {
            logDebug("btnErrorActionTwo onClickThrottle", TAG)
            actionTwoClickListener?.invoke()
        }
    }

}