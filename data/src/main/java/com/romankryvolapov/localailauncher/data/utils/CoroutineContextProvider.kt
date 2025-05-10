/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.utils

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class CoroutineContextProvider {

    open val main: CoroutineContext by lazy { Dispatchers.Main }

    open val io: CoroutineContext by lazy { Dispatchers.IO }

    open val default: CoroutineContext by lazy { Dispatchers.Default }

}