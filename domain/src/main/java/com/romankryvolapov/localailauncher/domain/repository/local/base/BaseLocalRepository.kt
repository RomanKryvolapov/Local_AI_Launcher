/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.repository.local.base

import android.os.Bundle
import android.os.Parcelable

interface BaseLocalRepository<T> : Parcelable {

    suspend fun saveState(bundle: Bundle)

    suspend fun restoreState(bundle: Bundle)

    suspend fun clear()

}