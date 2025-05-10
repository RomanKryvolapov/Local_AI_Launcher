/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.NavActivityDirections
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.data.infrastructure.LaunchEngines
import com.romankryvolapov.localailauncher.domain.DEBUG_LOGOUT_FROM_PREFERENCES
import com.romankryvolapov.localailauncher.domain.Models
import com.romankryvolapov.localailauncher.domain.models.base.onFailure
import com.romankryvolapov.localailauncher.domain.models.base.onSuccess
import com.romankryvolapov.localailauncher.domain.models.common.ApplicationInfo
import com.romankryvolapov.localailauncher.domain.models.common.ApplicationLanguage
import com.romankryvolapov.localailauncher.domain.usecase.CopyAllAssetFilesToUserFilesUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mediapipe.StartEngineMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mlcllm.StartMLCEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.tensorflow.StartEngineTensorFlowUseCase
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

    private val engines: LaunchEngines by inject()
    private val startMLCEngineUseCase: StartMLCEngineUseCase by inject()
    private val startEngineMediaPipeUseCase: StartEngineMediaPipeUseCase by inject()
    private val startEngineTensorFlowUseCase: StartEngineTensorFlowUseCase by inject()
    private val copyAllAssetFilesToUserFilesUseCase: CopyAllAssetFilesToUserFilesUseCase by inject()

    private val messages = StringBuilder()

    private val _messagesLiveData = MutableLiveData<String>()
    val messagesLiveData = _messagesLiveData.readOnly()

    @Volatile
    private var isMLCEngineStarted = false

    @Volatile
    private var isMediaPipeEngineStarted = false

    @Volatile
    private var isTensorFlowEngineStarted = false

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)

        if (BuildConfig.DEBUG && DEBUG_LOGOUT_FROM_PREFERENCES) {
            preferences.logoutFromPreferences()
            logDebug("logoutFromPreferences", TAG)
        }

        addMessage("OpenCL available: ${hasOpenCLLibrary()}")

        val applicationInfo = preferences.readApplicationInfo() ?: ApplicationInfo(
            accessToken = "",
            isFirstFun = true,
            refreshToken = "",
            applicationLanguage = ApplicationLanguage.EN,
        )

        addMessage("Is first run: ${applicationInfo.isFirstFun}")
        // TODO if (applicationInfo.isFirstFun) {
        if (true) {
            copyAllAssetFilesToUserFilesUseCase.invoke(
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
                    startEngines()
                }.onFailure { error, title, message, responseCode, errorType ->
                    addMessage("Model not copied, error: $error")
                    preferences.saveApplicationInfo(
                        applicationInfo.copy(
                            isFirstFun = true
                        )
                    )
                }
            }.launchInScope(viewModelScope)
        }
    }

    private fun startEngines() {
        startMLCEngine()
        startMediaPipeEngine()
        startTensorFlowEngine()
    }

    private fun startMLCEngine() {
        addMessage("Start MLC engine")
        val modelFile = File(
            currentContext.get().filesDir,
            Models.MLCEngineModel.GEMMA_3_1B_QAT.modelName
        )
        if (!modelFile.exists()) {
            addMessage("MLC Engine error: file not exist")
            return
        }
        startMLCEngineUseCase.invoke(
            modelFile = modelFile,
            modelLib = Models.MLCEngineModel.GEMMA_3_1B_QAT.modelLib,
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                addMessage("MLC Engine started")
                engines.mlcEngine = result.model
                if (isMLCEngineStarted && isMediaPipeEngineStarted && isTensorFlowEngineStarted) {
                    openMainTabs()
                }
            }.onFailure { error, title, message, responseCode, errorType ->
                addMessage("MLC Engine error: $error")
            }
        }.launchInScope(viewModelScope)
    }

    private fun startMediaPipeEngine() {
        addMessage("Start MediaPipe engine")
        val modelFile = File(
            currentContext.get().filesDir,
            Models.MediaPipeModel.GEMMA_3_1B_QAT.modelName
        )
        if (!modelFile.exists()) {
            addMessage("MediaPipe Engine error: file not exist")
            return
        }
        startEngineMediaPipeUseCase.invoke(
            modelFile = modelFile,
            context = currentContext.get(),
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                addMessage("MediaPipe Engine started")
                engines.llmInference = result.model
                if (isMLCEngineStarted && isMediaPipeEngineStarted && isTensorFlowEngineStarted) {
                    openMainTabs()
                }
            }.onFailure { error, title, message, responseCode, errorType ->
                addMessage("MediaPipe Engine error: $error")
            }
        }.launchInScope(viewModelScope)
    }

    private fun startTensorFlowEngine() {
        addMessage("Start TensorFlow engine")
        val modelFile = File(
            currentContext.get().filesDir,
            Models.MediaPipeModel.GEMMA_3_1B_QAT.modelName
        )
        if (!modelFile.exists()) {
            addMessage("TensorFlow Engine error: file not exist")
            return
        }
        startEngineTensorFlowUseCase.invoke(
            modelFile = modelFile,
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                addMessage("MediaPipe Engine started")
                engines.interpreter = result.model
                if (isMLCEngineStarted && isMediaPipeEngineStarted && isTensorFlowEngineStarted) {
                    openMainTabs()
                }
            }.onFailure { error, title, message, responseCode, errorType ->
                addMessage("MediaPipe Engine error: $error")
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