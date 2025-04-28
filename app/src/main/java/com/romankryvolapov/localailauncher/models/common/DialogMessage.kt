/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.romankryvolapov.localailauncher.R

data class DialogMessage(
    val messageID: String? = null,
    val title: StringSource? = null,
    val message: StringSource,
    val state: State = State.ERROR,
    val gravity: Gravity = Gravity.START,
    val positiveButtonText: StringSource? = null,
    val negativeButtonText: StringSource? = null,
    @DrawableRes val icon: Int? = R.drawable.ic_error,
) {

    enum class State {
        ERROR,
        SUCCESS
    }

    enum class Gravity {
        START,
        CENTER
    }

    companion object {
        /**
         * Red error banners with [message] and error [icon] and gravity start.
         */
        fun error(
            message: String,
            @DrawableRes icon: Int = R.drawable.ic_error
        ): DialogMessage {
            return DialogMessage(
                message = StringSource(message),
                icon = icon,
                state = State.ERROR
            )
        }

        fun error(
            @StringRes message: Int,
            @DrawableRes icon: Int = R.drawable.ic_error
        ): DialogMessage {
            return DialogMessage(
                message = StringSource(message),
                icon = icon,
                state = State.ERROR
            )
        }

        /**
         * Green success banners with [message] and success [icon] and gravity start.
         */
        fun success(
            message: String,
            @DrawableRes icon: Int = R.drawable.ic_success
        ): DialogMessage {
            return DialogMessage(
                message = StringSource(message),
                icon = icon,
                state = State.SUCCESS
            )
        }

        fun success(
            @StringRes message: Int,
            @DrawableRes icon: Int = R.drawable.ic_success
        ): DialogMessage {
            return DialogMessage(
                message = StringSource(message),
                icon = icon,
                state = State.SUCCESS
            )
        }

        /**
         * Green success banners with simple [message] in the center of the banner.
         */
        fun successCenter(message: String): DialogMessage {
            return DialogMessage(
                message = StringSource(message),
                icon = null,
                state = State.SUCCESS,
                gravity = Gravity.CENTER
            )
        }

        fun successCenter(@StringRes message: Int): DialogMessage {
            return DialogMessage(
                message = StringSource(message),
                icon = null,
                state = State.SUCCESS,
                gravity = Gravity.CENTER
            )
        }
    }
}