/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener

inline fun AppCompatEditText.setTextChangeListener(
    crossinline text: (String) -> Unit
) {
    addTextChangedListener { editable ->
        text(editable?.toString() ?: "")
    }
}

inline fun AppCompatEditText.setFocusChangeListener(
    crossinline hasFocus: (Boolean) -> Unit
) {
    setOnFocusChangeListener { _, focus ->
        hasFocus(focus)
    }
}

fun AppCompatEditText.moveCursorToEnd() {
    text?.let {
        setSelection(it.length)
    }
}

fun AppCompatEditText.hideKeyboardOnDone() {
    setOnEditorActionListener { view, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_NEXT,
            EditorInfo.IME_ACTION_DONE -> {
                view.hideKeyboard()
                true
            }

            else -> false
        }
    }
}

inline fun AppCompatEditText.onDonePressed(
    hideKeyboard: Boolean = true,
    crossinline onDone: () -> Unit,
) {
    setOnEditorActionListener { view, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_NEXT,
            EditorInfo.IME_ACTION_DONE -> {
                if (hideKeyboard) {
                    view.hideKeyboard()
                }
                onDone()
                true
            }

            else -> false
        }
    }
}