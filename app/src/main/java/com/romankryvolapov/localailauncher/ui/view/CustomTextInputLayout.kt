/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.data.extensions.getParcelableCompat
import com.romankryvolapov.localailauncher.extensions.color
import com.romankryvolapov.localailauncher.extensions.drawable
import com.romankryvolapov.localailauncher.models.common.StringSource
import kotlinx.parcelize.Parcelize

class CustomTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    companion object {
        private const val SUPER_STATE = "super_state"
        private const val CURRENT_RADIUS_STATE = "current_radius_state"
    }

    private val defaultBg = context.drawable(R.drawable.bg_input_layout_default)

    private val mediumBgRadius6 = context.drawable(R.drawable.bg_input_layout_default_6)

    private val errorBg = context.drawable(R.drawable.bg_input_layout_error)

    private val errorMediumBgRadius6 = context.drawable(R.drawable.bg_input_layout_error_6)

    private var currentRadius = InputBackgroundRadius.DEFAULT_RADIUS

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            error = null
        }
    }

    init {
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.CustomTextInputLayout) {
            getInt(
                R.styleable.CustomTextInputLayout_custom_text_input_radius,
                InputBackgroundRadius.DEFAULT_RADIUS.ordinal
            ).let { radiusIndex ->
                changeInputRadiusBackground(InputBackgroundRadius.values()[radiusIndex])
            }
        }
    }

    private fun changeInputRadiusBackground(radius: InputBackgroundRadius) {
        currentRadius = radius
        editText?.background = getCorrectEditTextBackground(radius, isErrorEnabled)
    }

    private fun getCorrectEditTextBackground(
        radius: InputBackgroundRadius,
        isErrorEnabled: Boolean
    ): Drawable? {
        return takeIf { isErrorEnabled }?.let {
            when (radius) {
                InputBackgroundRadius.DEFAULT_RADIUS -> errorBg
                InputBackgroundRadius.MEDIUM_RADIUS -> errorMediumBgRadius6
            }
        } ?: when (radius) {
            InputBackgroundRadius.DEFAULT_RADIUS -> defaultBg
            InputBackgroundRadius.MEDIUM_RADIUS -> mediumBgRadius6
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        editText?.background = getCorrectEditTextBackground(currentRadius, isErrorEnabled)
        editText?.addTextChangedListener(textWatcher)
    }

    override fun setError(errorText: CharSequence?) {
        super.setError(errorText)
        isErrorEnabled = errorText.isNullOrBlank().not()
    }

    fun setErrorMessage(errorMessage: StringSource?) {
        super.setError(errorMessage?.getString(context) ?: "")
        isErrorEnabled = errorMessage != null
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        if (enabled) {
            // Just hide container with error text view
            getChildAt(1).isVisible = error.isNullOrBlank().not()
        }
        editText?.background = getCorrectEditTextBackground(currentRadius, enabled)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        editText?.removeTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            SUPER_STATE to super.onSaveInstanceState(),
            CURRENT_RADIUS_STATE to currentRadius,
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState = state
        (state as? Bundle)?.let {
            changeInputRadiusBackground(it.getParcelableCompat(CURRENT_RADIUS_STATE)!!)
            superState = it.getParcelableCompat(SUPER_STATE)
        }
        super.onRestoreInstanceState(superState)
    }

    fun setHintTextColorResource(@ColorRes resource: Int) {
        defaultHintTextColor = ColorStateList.valueOf(context.color(resource))
    }

    fun setHintTextResource(@StringRes resource: Int){
        hint = context.getString(resource)
    }

    fun setEndIconStyle(mode: Int, tintList: ColorStateList? = null, @DrawableRes drawable: Int) {
        endIconMode = mode
        setEndIconTintList(tintList)
        endIconDrawable = AppCompatResources.getDrawable(context, drawable)
    }

    @Parcelize
    private enum class InputBackgroundRadius : Parcelable {
        DEFAULT_RADIUS,
        MEDIUM_RADIUS
    }
}