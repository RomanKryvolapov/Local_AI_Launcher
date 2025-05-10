/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import javax.crypto.Cipher

interface SupportBiometricManager {

    val onBiometricErrorLiveData: LiveData<Unit>

    val onBiometricTooManyAttemptsLiveData: LiveData<Unit>

    val onBiometricSuccessLiveData: LiveData<Cipher?>

    fun hasBiometrics(): Boolean

    fun setupBiometricManager(fragment: Fragment)

    fun authenticate(cipher: Cipher?)

    fun cancelAuthentication()
}