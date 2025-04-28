/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.common

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartDestination(
    @IdRes val destination: Int,

    /**
     * Use this variable to pass arguments to the start destination of
     * the [destination] graph.
     */
    val arguments: Bundle? = null
) : Parcelable