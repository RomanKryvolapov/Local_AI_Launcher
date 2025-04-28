/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import ai.mlc.mlcllm.MLCEngine
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.NavActivityDirections
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.DEBUG_LOGOUT_FROM_PREFERENCES
import com.romankryvolapov.localailauncher.domain.DEBUG_PRINT_PREFERENCES_INFO
import com.romankryvolapov.localailauncher.domain.MODEL_LIB
import com.romankryvolapov.localailauncher.domain.MODEL_NAME
import com.romankryvolapov.localailauncher.domain.usecase.CopyAssetsToFileUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
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

    private val copyAssetsToFileUseCase: CopyAssetsToFileUseCase by inject()
    private val engine: MLCEngine by inject()

    private val messages = StringBuilder()

    private val _messagesLiveData = MutableLiveData<String>()
    val messagesLiveData = _messagesLiveData.readOnly()

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)

        if (BuildConfig.DEBUG && DEBUG_LOGOUT_FROM_PREFERENCES) {
            preferences.logoutFromPreferences()
            logDebug("logoutFromPreferences", TAG)
        }
        if (BuildConfig.DEBUG && DEBUG_PRINT_PREFERENCES_INFO) {
            logDebug("PRINT_PREFERENCES_INFO", TAG)
            val applicationInfo = preferences.readApplicationInfo()
        }

        addMessage("OpenCL available: ${hasOpenCLLibrary()}")

        val isFirstRun = preferences.readApplicationInfo()?.isFirstFun == true

        addMessage("Is first run: $isFirstRun")

        if (isFirstRun) {
            copyAssetsToFileUseCase.invoke(
                modelName = MODEL_NAME,
                filesDir = currentContext.get().filesDir,
                assetManager = currentContext.get().assets,
            ).onEach { result ->
                if (result) {
                    addMessage("Model copied")
                    startEngine()
                } else {
                    addMessage("Model not copied")
                }
            }
        } else {
            startEngine()
        }
    }

    private fun startEngine() {
        addMessage("Start engine")
        try {
            val modelDir = File(currentContext.get().filesDir, MODEL_NAME)
            val modelPath = modelDir.absolutePath
            engine.reload(modelPath, MODEL_LIB)
            addMessage("Engine reloaded")
            openMainTabs()
        } catch (e: Exception) {
            addMessage("Engine error: ${e.message}")
        }
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