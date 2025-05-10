/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isInvisible
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.LayoutCustomNumericKeyboardBinding
import com.romankryvolapov.localailauncher.extensions.color
import com.romankryvolapov.localailauncher.extensions.onClick
import com.romankryvolapov.localailauncher.extensions.onClickThrottle

class PassCodeKeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var maxNumbersLimit = 4
    private var passCodeBuilder = StringBuilder()

    var onTextChangedCallback: ((String) -> Unit)? = null
    var onFingerprintCallback: (() -> Unit)? = null

    private val binding = LayoutCustomNumericKeyboardBinding.inflate(LayoutInflater.from(context),
        this)

    init {
        clipToPadding = false
        setupControls()
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.KeyboardView) {
            getInteger(
                R.styleable.KeyboardView_keyboard_view_numbers, -1).let { numbers ->
                if (numbers != -1) {
                    setMaxNumbersLimit(numbers)
                }
            }
        }
    }

    private fun setupControls() {
        // Setup keyboard
        binding.btn1.onClick { add(1) }
        binding.btn2.onClick { add(2) }
        binding.btn3.onClick { add(3) }
        binding.btn4.onClick { add(4) }
        binding.btn5.onClick { add(5) }
        binding.btn6.onClick { add(6) }
        binding.btn7.onClick { add(7) }
        binding.btn8.onClick { add(8) }
        binding.btn9.onClick { add(9) }
        binding.btn0.onClick { add(0) }
        binding.btnDelete.onClick { removeLast() }
        binding.btnFingerprint.onClickThrottle { onFingerprintCallback?.invoke() }
    }

    fun lockKeyboard() {
        setKeyboardEnabled(false)
    }

    fun unlockKeyboard() {
        setKeyboardEnabled(true)
    }

    fun setTextWithoutListener(text: String) {
        passCodeBuilder.clear()
        passCodeBuilder.append(text)
        binding.btnDelete.isInvisible = text.isEmpty()
    }

    private fun setKeyboardEnabled(isEnabled: Boolean) {
        binding.btn1.isEnabled = isEnabled
        binding.btn2.isEnabled = isEnabled
        binding.btn3.isEnabled = isEnabled
        binding.btn4.isEnabled = isEnabled
        binding.btn5.isEnabled = isEnabled
        binding.btn6.isEnabled = isEnabled
        binding.btn7.isEnabled = isEnabled
        binding.btn8.isEnabled = isEnabled
        binding.btn9.isEnabled = isEnabled
        binding.btn0.isEnabled = isEnabled
        binding.btnDelete.isEnabled = isEnabled
        binding.btnFingerprint.isEnabled = isEnabled
    }

    fun setMaxNumbersLimit(limit: Int) {
        if (limit < 1) throw IllegalArgumentException("Limit can't be less then 1")
        maxNumbersLimit = limit
    }

    // Every time you complete the input, you should clear the saved text here.
    fun clearKeyboard() {
        passCodeBuilder.clear()
        binding.btnDelete.isInvisible = true
    }

    // Show or hide fingerprint button.
    fun showFingerprintButton(isShow: Boolean) {
        binding.btnFingerprint.isInvisible = !isShow
    }

    fun setInputEnabled(enabled: Boolean) {
        isEnabled = enabled
        children.forEach {
            if (it is TextView) {
                it.isClickable = enabled
                it.isEnabled = enabled
                val color = context.color(
                    if (!enabled) R.color.color_gray_5 else R.color.color_white
                )
                it.setTextColor(color)
            }
        }
    }

    // Type in some number to pass code.
    private fun add(number: Int) {
        if (passCodeBuilder.length < maxNumbersLimit) {
            passCodeBuilder.append(number.toString())
            onTextChangedCallback?.invoke(passCodeBuilder.toString())
        }

        binding.btnDelete.isInvisible = false
    }

    // Delete last number from pass code
    private fun removeLast() {
        if (passCodeBuilder.isNotEmpty()) {
            passCodeBuilder.deleteCharAt(passCodeBuilder.lastIndex)
            onTextChangedCallback?.invoke(passCodeBuilder.toString())
        }
        binding.btnDelete.isInvisible = passCodeBuilder.isEmpty()
    }

}