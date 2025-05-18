/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.NavActivityDirections
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.onFailure
import com.romankryvolapov.localailauncher.common.models.common.onLoading
import com.romankryvolapov.localailauncher.common.models.common.onSuccess
import com.romankryvolapov.localailauncher.domain.DEBUG_LOGOUT_FROM_PREFERENCES
import com.romankryvolapov.localailauncher.domain.Model
import com.romankryvolapov.localailauncher.domain.models
import com.romankryvolapov.localailauncher.domain.usecase.ClearFilesDirectoryUseCase
import com.romankryvolapov.localailauncher.domain.usecase.CopyAllAssetFilesToUserFilesUseCase
import com.romankryvolapov.localailauncher.domain.usecase.DownloadMultipleToExternalFilesDirectoryUseCase
import com.romankryvolapov.localailauncher.domain.usecase.DownloadToExternalFilesDirectoryUseCase
import com.romankryvolapov.localailauncher.extensions.launchInScope
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.llama.usecase.GetGGUFModelParametersUseCase
import com.romankryvolapov.localailauncher.models.splash.SplashLoadingMessageUi
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.inject
import java.io.File
import java.util.UUID
import kotlin.system.exitProcess

class SplashViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "SplashViewModelTag"
    }

    private val copyAllAssetFilesToUserFilesUseCase: CopyAllAssetFilesToUserFilesUseCase by inject()
    private val getGGUFModelParametersUseCase: GetGGUFModelParametersUseCase by inject()
    private val clearFilesDirectoryUseCase: ClearFilesDirectoryUseCase by inject()
    private val downloadToExternalFilesDirectoryUseCase: DownloadToExternalFilesDirectoryUseCase by inject()
    private val downloadMultipleToExternalFilesDirectoryUseCase: DownloadMultipleToExternalFilesDirectoryUseCase by inject()

    private val messages = mutableMapOf<UUID, SplashLoadingMessageUi>()

    private val _messagesLiveData = MutableLiveData<List<SplashLoadingMessageUi>>()
    val messagesLiveData = _messagesLiveData.readOnly()

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)
        if (BuildConfig.DEBUG && DEBUG_LOGOUT_FROM_PREFERENCES) {
            preferences.logoutFromPreferences()
            logDebug("logoutFromPreferences", TAG)
        }
        addMessage(
            model = SplashLoadingMessageUi(
                message = "OpenCL available on device: ${hasOpenCLLibrary()}"
            )
        )
        copyAllAssetFilesToUserFiles()
    }


    private fun copyAllAssetFilesToUserFiles() {
        logDebug("copyAllAssetFilesToUserFiles", TAG)
        addMessage(
            model = SplashLoadingMessageUi(
                message = "Copy models files do device"
            )
        )
        val subfolder = "models"
        val filesDir = File(
            currentContext.get().getExternalFilesDir(null),
            subfolder
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val messageID = UUID.randomUUID()
        copyAllAssetFilesToUserFilesUseCase.invoke(
            filesDir = filesDir,
            assetManager = currentContext.get().assets,
        ).onEach { result ->
            result.onLoading { model, _ ->
                logDebug("onLoading: $model", TAG)
                addMessage(
                    id = messageID,
                    model = SplashLoadingMessageUi(
                        id = messageID,
                        message = model.toString(),
                    )
                )
            }.onSuccess { model, _, _ ->
                logDebug("onSuccess: $model", TAG)
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = "Coped all files to device",
                    )
                )
                downloadMultipleFromHuggingFace()
            }.onFailure { error, _, message, _, _ ->
                logError("onFailure", message, TAG)
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = "Models files not copied, error: $error",
                    )
                )
            }
        }.launchInScope(viewModelScope)
    }


    private fun downloadMultipleFromHuggingFace() {
        logDebug("downloadMultipleFromHuggingFace", TAG)
        // /storage/emulated/0/Android/data/com.romankryvolapov.localailauncher/files/models/...
        // https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task
        // https://huggingface.co/lmstudio-community/gemma-3-4B-it-qat-GGUF/resolve/main/gemma-3-4B-it-QAT-Q4_0.gguf
        val models = models.filter {
            it.fileUrl != null
        }
        val messageID = UUID.randomUUID()
        downloadMultipleToExternalFilesDirectoryUseCase.invoke(
            models = models,
            context = currentContext.get(),
        ).onEach { result ->
            result.onLoading { model, message ->
                logDebug("onLoading: $model", TAG)
                addMessage(
                    id = messageID,
                    model = SplashLoadingMessageUi(
                        id = messageID,
                        message = message.toString(),
                    )
                )
            }.onSuccess { model, message, _ ->
                logDebug("onSuccess: $model", TAG)
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = message.toString(),
                    )
                )
                checkEnginesFiles()
            }.onFailure { _, _, message, _, _ ->
                logError("onFailure", message, TAG)
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = message.toString(),
                    )
                )
            }
        }.launchInScope(viewModelScope)
    }


    private fun downloadFromHuggingFace() {
        logDebug("downloadFromHuggingFace", TAG)
        // /storage/emulated/0/Android/data/com.romankryvolapov.localailauncher/files/models/...
        val model = models.filterIsInstance<Model.LlamaCppModel>().first()
        val fileUrl = model.fileUrl
        if (fileUrl == null) {
            addMessage(
                model = SplashLoadingMessageUi(
                    message = "Download model file error, fileUrl is null",
                )
            )
            return
        }
        val fileName = fileUrl.toUri().lastPathSegment
        val subfolder = "models/gguf"
        if (fileName == null) {
            addMessage(
                model = SplashLoadingMessageUi(
                    message = "Download model file error, unknown file name in url $fileUrl",
                )
            )
            return
        }
        val filesDir = File(
            currentContext.get().getExternalFilesDir(null),
            subfolder
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val file = File(filesDir, fileName)
        if (file.exists()) {
            addMessage(
                model = SplashLoadingMessageUi(
                    message = "Model file $fileName exist, not need download",
                )
            )
            checkEnginesFiles()
            return
        }
        addMessage(
            model = SplashLoadingMessageUi(
                message = "Download model file $fileName to device",
            )
        )
        val messageID = UUID.randomUUID()
        downloadToExternalFilesDirectoryUseCase.invoke(
            model = model,
            context = currentContext.get(),
        ).onEach { result ->
            result.onLoading { model, message ->
                logDebug("onLoading: $model", TAG)
                addMessage(
                    id = messageID,
                    model = SplashLoadingMessageUi(
                        id = messageID,
                        message = message.toString(),
                    )
                )
            }.onSuccess { model, message, _ ->
                logDebug("onSuccess: $model", TAG)
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = message.toString(),
                    )
                )
                checkEnginesFiles()
            }.onFailure { _, _, message, _, _ ->
                logError("onFailure", message, TAG)
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = message.toString(),
                    )
                )
            }
        }.launchInScope(viewModelScope)
    }

    private fun checkEnginesFiles() {
        models.forEach { model ->
            if (model.file.exists()) {
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = "File ${model.engineName} ${model.modelName} found"
                    )
                )
            } else {
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = "File ${model.engineName} ${model.modelName} not found"
                    )
                )
                return
            }
        }
        openMainTabs()
//        getModelData()
    }

//    private fun getModelData() {
//        val modelFile = File(
//            internalFilesDirectory,
//            models[0].filePath
//        )
//        getGGUFModelParametersUseCase.invoke(
//            modelFile = modelFile
//        ).onEach { result ->
//            result.onSuccess { _, _, _ ->
//                openMainTabs()
//            }.onFailure { _, _, message, _, _ ->
//                logError("$message", message, TAG)
//            }
//        }.launchInScope(viewModelScope)
//    }

//    private fun loadModelFile(filename: String): MappedByteBuffer {
//        val fileDescriptor = currentContext.get().assets.openFd(filename)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }

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


    private fun clearFilesDirectory() {
        logDebug("clearFilesDirectory", TAG)
        clearFilesDirectoryUseCase.invoke(
            filesDir = currentContext.get().filesDir,
        ).onEach { result ->
            result.onSuccess { _, _, _ ->
                addMessage(
                    model = SplashLoadingMessageUi(
                        message = "User files cleared",
                    )
                )
            }
        }.launchInScope(viewModelScope)
    }

    private fun hasOpenCLLibrary(): Boolean {
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

    private fun addMessage(
        id: UUID = UUID.randomUUID(),
        model: SplashLoadingMessageUi
    ) {
        logDebug("addMessage: ${model.message}", TAG)
        messages[id] = model
        _messagesLiveData.setValueOnMainThread(messages.values.toList())
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