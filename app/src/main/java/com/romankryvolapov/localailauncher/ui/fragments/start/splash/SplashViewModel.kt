/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.NavActivityDirections
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.DEBUG_LOGOUT_FROM_PREFERENCES
import com.romankryvolapov.localailauncher.domain.DEBUG_PRINT_PREFERENCES_INFO
import com.romankryvolapov.localailauncher.domain.MODEL_LIB
import com.romankryvolapov.localailauncher.domain.MODEL_NAME
import com.romankryvolapov.localailauncher.domain.models.base.onFailure
import com.romankryvolapov.localailauncher.domain.models.base.onSuccess
import com.romankryvolapov.localailauncher.domain.models.common.ApplicationInfo
import com.romankryvolapov.localailauncher.domain.models.common.ApplicationLanguage
import com.romankryvolapov.localailauncher.domain.usecase.CopyAssetsToFileUseCase
import com.romankryvolapov.localailauncher.domain.usecase.StartEngineUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.launchInScope
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.inject
import java.io.File
import kotlin.system.exitProcess

class SplashViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "SplashViewModelTag"
    }

    private val startEngineUseCase: StartEngineUseCase by inject()
    private val copyAssetsToFileUseCase: CopyAssetsToFileUseCase by inject()

    private val messages = StringBuilder()

    private val _messagesLiveData = MutableLiveData<String>()
    val messagesLiveData = _messagesLiveData.readOnly()

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)

        if (BuildConfig.DEBUG && DEBUG_LOGOUT_FROM_PREFERENCES) {
            preferences.logoutFromPreferences()
            logDebug("logoutFromPreferences", TAG)
        }

        addMessage("OpenCL available: ${hasOpenCLLibrary()}")

        val applicationInfo = preferences.readApplicationInfo() ?: ApplicationInfo(
            isFirstFun = true,
            applicationLanguage = ApplicationLanguage.EN,
        )

        addMessage("Is first run: ${applicationInfo.isFirstFun}")

        if (applicationInfo.isFirstFun) {
            copyAssetsToFileUseCase.invoke(
                modelName = MODEL_NAME,
                filesDir = currentContext.get().filesDir,
                assetManager = currentContext.get().assets,
            ).onEach { result ->
                result.onSuccess { model, _, responseCode ->
                    addMessage("Model copied")
                    preferences.saveApplicationInfo(
                        applicationInfo.copy(
                            isFirstFun = false
                        )
                    )
                    startEngine()
                }.onFailure { error, title, message, responseCode, errorType ->
                    addMessage("Model not copied, error: $error")
                    preferences.saveApplicationInfo(
                        applicationInfo.copy(
                            isFirstFun = true
                        )
                    )
                }
            }.launchInScope(viewModelScope)
        } else {
            startEngine()
        }
    }

    private fun startEngine() {
        addMessage("Start engine")
        startEngineUseCase.invoke(
            modelLib = MODEL_LIB,
            modelName = MODEL_NAME,
            filesDir = currentContext.get().filesDir,
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                addMessage("Engine started")
                openMainTabs()
            }.onFailure { error, title, message, responseCode, errorType ->
                addMessage("Engine error: $error")
            }
        }.launchInScope(viewModelScope)
    }

    fun findOpenCLPaths(): String? {
        val paths = listOf(
            "/vendor/lib64/libOpenCL.so",
            "/system/vendor/lib64/libOpenCL.so",
            "/system/lib64/libOpenCL.so",
            "/system_ext/lib64/libOpenCL.so"
        )
        val foundPaths = paths.filter { path ->
            File(path).exists()
        }
        return if (foundPaths.isNotEmpty()) {
            foundPaths.joinToString(separator = ", ")
        } else {
            null
        }
    }

    fun hasOpenCLLibrary(): Boolean {
        val paths = listOf(
            "/vendor/lib64/libOpenCL.so",
            "/system/vendor/lib64/libOpenCL.so",
            "/system/lib64/libOpenCL.so",
            "/system_ext/lib64/libOpenCL.so"
        )
        return paths.any { path ->
            File(path).exists()
        }
    }

    private fun addMessage(message: String) {
        Log.d(TAG, "addMessage: $message")
        messages.append("\n")
        messages.append(message)
        _messagesLiveData.setValueOnMainThread(messages.toString())
    }

    private fun openMainTabs() {
        navigateInActivity(
            NavActivityDirections.toMainTabsFlowFragment(
                openOnTab = R.id.nav_main_tab_one
            )
        )
    }

    override fun onBackPressed() {
        logDebug("onBackPressed", TAG)
        exitProcess(0)
    }


}