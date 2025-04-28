/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.DrawableCompat
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.LayoutToolbarBinding
import com.romankryvolapov.localailauncher.extensions.onClickThrottle
import com.romankryvolapov.localailauncher.extensions.pxDimen
import com.romankryvolapov.localailauncher.extensions.setTextSource
import com.romankryvolapov.localailauncher.extensions.showSpinner
import com.romankryvolapov.localailauncher.extensions.tintColor
import com.romankryvolapov.localailauncher.extensions.tintRes
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.models.list.CommonSpinnerMenuItemUi
import com.romankryvolapov.localailauncher.models.list.CommonSpinnerUi

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "CustomToolbarTag"
    }

    private val binding = LayoutToolbarBinding.inflate(LayoutInflater.from(context), this)

    var navigationClickListener: (() -> Unit)? = null

    var settingsClickListener: (() -> Unit)? = null

    init {
        setupView()
        setupAttributes(attrs)
        setupControls()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = context.pxDimen(R.dimen.toolbar_height)
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

    private fun setupView() {
        orientation = HORIZONTAL
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.CustomToolbar) {
            val title = getString(R.styleable.CustomToolbar_toolbar_title)
            if (!title.isNullOrEmpty()) {
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvTitle.text = title
            } else {
                binding.tvTitle.visibility = View.INVISIBLE
            }
            val toolbarNavigationIcon =
                getDrawable(R.styleable.CustomToolbar_toolbar_navigation_icon)
            if (toolbarNavigationIcon != null) {
                binding.icNavigation.visibility = View.VISIBLE
                binding.icNavigation.setImageDrawable(toolbarNavigationIcon)
            } else {
                binding.icNavigation.visibility = View.INVISIBLE
            }
            val toolbarSettingsIcon = getDrawable(R.styleable.CustomToolbar_toolbar_settings_icon)
            if (toolbarSettingsIcon != null) {
                binding.icSettings.visibility = View.VISIBLE
                binding.icSettings.setImageDrawable(toolbarSettingsIcon)
            } else {
                binding.icSettings.visibility = View.INVISIBLE
            }
            val settingsText = getString(R.styleable.CustomToolbar_toolbar_settings_text)
            if (!settingsText.isNullOrEmpty()) {
                binding.btnSettings.visibility = View.VISIBLE
                binding.btnSettings.text = settingsText
            } else {
                binding.btnSettings.visibility = View.GONE
            }
            val toolbarColor =
                getColor(R.styleable.CustomToolbar_toolbar_background_color, 0)
            if (toolbarColor != 0) {
                binding.layoutNavigation.setBackgroundColor(toolbarColor)
                binding.layoutSettings.setBackgroundColor(toolbarColor)
            }
            val textColor = getColor(R.styleable.CustomToolbar_toolbar_elements_color, 0)
            if (textColor != 0) {
                binding.icNavigation.tintColor(textColor)
                binding.tvTitle.setTextColor(textColor)
                binding.icSettings.tintColor(textColor)
                binding.btnSettings.setTextColor(textColor)
                val drawables = binding.btnSettings.compoundDrawablesRelative
                val drawableEnd = drawables[2]
                if (drawableEnd != null) {
                    val wrappedDrawable = DrawableCompat.wrap(drawableEnd).mutate()
                    DrawableCompat.setTint(wrappedDrawable, textColor)
                    binding.btnSettings.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        drawables[0],
                        drawables[1],
                        wrappedDrawable,
                        drawables[3]
                    )
                }
            }
        }
    }

    private fun setupControls() {
        binding.icNavigation.onClickThrottle {
            navigationClickListener?.invoke()
        }
        binding.btnSettings.onClickThrottle {
            settingsClickListener?.invoke()
        }
        binding.icSettings.onClickThrottle {
            settingsClickListener?.invoke()
        }
    }

    fun setTitleText(text: StringSource) {
        binding.tvTitle.setTextSource(text)
        binding.tvTitle.visibility = View.VISIBLE
    }

    fun setSettingsText(text: StringSource) {
        binding.btnSettings.setTextSource(text)
        binding.btnSettings.visibility = View.VISIBLE
    }

    fun setSettingsIcon(
        @DrawableRes settingsIconRes: Int = R.drawable.ic_arrow_down,
        @ColorRes settingsIconColorRes: Int = R.color.color_white,
        settingsClickListener: (() -> Unit),
    ) {
        binding.icSettings.setImageResource(settingsIconRes)
        binding.icSettings.tintRes(settingsIconColorRes)
        binding.icSettings.visibility = View.VISIBLE
        this.settingsClickListener = settingsClickListener
    }

    fun hideSettingsIcon() {
        binding.icSettings.visibility = View.INVISIBLE
        settingsClickListener = null
    }

    fun setNavigationIcon(
        @DrawableRes navigationIconRes: Int = R.drawable.ic_arrow_left,
        @ColorRes navigationIconColorRes: Int = R.color.color_white,
        navigationClickListener: (() -> Unit),
    ) {
        binding.icNavigation.setImageResource(navigationIconRes)
        binding.icNavigation.tintRes(navigationIconColorRes)
        binding.icNavigation.visibility = View.VISIBLE
        this.navigationClickListener = navigationClickListener
    }

    fun hideNavigationIcon() {
        binding.icNavigation.visibility = View.INVISIBLE
        navigationClickListener = null
    }

    fun showSettingsSpinner(
        model: CommonSpinnerUi,
        clickListener: ((model: CommonSpinnerMenuItemUi) -> Unit)
    ) {
        binding.icSettings.showSpinner(
            model = model,
            clickListener = clickListener,
        )
    }

}