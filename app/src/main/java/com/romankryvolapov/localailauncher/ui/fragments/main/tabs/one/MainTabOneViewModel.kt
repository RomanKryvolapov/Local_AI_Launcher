/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import androidx.annotation.ColorRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.data.infrastructure.LaunchEngines
import com.romankryvolapov.localailauncher.domain.Model
import com.romankryvolapov.localailauncher.domain.Model.MLCEngineModel
import com.romankryvolapov.localailauncher.domain.Model.MediaPipeModel
import com.romankryvolapov.localailauncher.domain.Model.OnnxModel
import com.romankryvolapov.localailauncher.domain.extensions.nextOrFirst
import com.romankryvolapov.localailauncher.domain.extensions.nextOrFirstOrCurrent
import com.romankryvolapov.localailauncher.domain.models
import com.romankryvolapov.localailauncher.domain.models.base.onFailure
import com.romankryvolapov.localailauncher.domain.models.base.onLoading
import com.romankryvolapov.localailauncher.domain.models.base.onSuccess
import com.romankryvolapov.localailauncher.domain.usecase.mediapipe.SendMessageMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mediapipe.StartEngineMediaPipeUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mlcllm.SendMessageMLCEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.mlcllm.StartMLCEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.onnx.SendMessageOnnxEngineUseCase
import com.romankryvolapov.localailauncher.domain.usecase.onnx.StartOnnxEngineUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.launchInScope
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.mappers.ChatMessageModelUiMapper
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.inject
import java.io.File
import java.time.temporal.TemporalAdjusters.next
import java.util.UUID

class MainTabOneViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabOneViewModelTag"
        private val dialogID = UUID.randomUUID()

        enum class EngineLoadingState(
            val message: String,
            @ColorRes val messageColourRes: Int
        ) {
            NOT_LOADED(">>Load<<", R.color.color_BF1212),
            LOADING("Loading...", R.color.color_018930),
            LOADED(">>Unload<<", R.color.color_0C53B2)
        }

        enum class EngineGeneratingState(
            val message: String,
            @ColorRes val messageColourRes: Int
        ) {
            READY("Start", R.color.color_0C53B2),
            IN_PROCESS("STOP", R.color.color_BF1212),
        }
    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_ONE

    private val engines: LaunchEngines by inject()

    private val chatMessageModelUiMapper: ChatMessageModelUiMapper by inject()

    private val startMLCEngineUseCase: StartMLCEngineUseCase by inject()
    private val sendMessageMLCEngineUseCase: SendMessageMLCEngineUseCase by inject()

    private val startEngineMediaPipeUseCase: StartEngineMediaPipeUseCase by inject()
    private val sendMessageMediaPipeUseCase: SendMessageMediaPipeUseCase by inject()

    private val startOnnxEngineUseCase: StartOnnxEngineUseCase by inject()
    private val sendMessageOnnxEngineUseCase: SendMessageOnnxEngineUseCase by inject()

    private val messagesMap = mutableMapOf<UUID, ChatMessageAdapterMarker>()

    private val _messagesLiveData = MutableLiveData<List<ChatMessageAdapterMarker>>()
    val messagesLiveData = _messagesLiveData.readOnly()

    private val _engineLiveData = MutableLiveData<String>()
    val engineLiveData = _engineLiveData.readOnly()

    private val _modelLiveData = MutableLiveData<String>()
    val modelLiveData = _modelLiveData.readOnly()

    private val _engineLoadStateLiveData = MutableLiveData<EngineLoadingState>()
    val engineLoadStateLiveData = _engineLoadStateLiveData.readOnly()

    private val _engineGeneratingStateLiveData =
        MutableLiveData<EngineGeneratingState>(EngineGeneratingState.READY)
    val engineGeneratingStateLiveData = _engineGeneratingStateLiveData.readOnly()

    private var selected: Model? = null

    private var engineLoadingState: EngineLoadingState = EngineLoadingState.NOT_LOADED

    override fun onFirstAttach() {
        _engineLoadStateLiveData.setValueOnMainThread(engineLoadingState)
        val applicationInfo = preferences.readApplicationInfo()
        selected = models[applicationInfo.selectedModelPosition]
        _engineLiveData.setValueOnMainThread(selected?.engineName ?: "Unknown engine")
        _modelLiveData.setValueOnMainThread(selected?.modelName ?: "Unknown model")
    }

    fun sendMessage(message: String) {
        logDebug("sendMessage message: $message", TAG)
        when (selected) {
            is MLCEngineModel -> {
                if (engines.mlcEngine == null) {
                    showErrorState(
                        title = StringSource(R.string.error_internal_error_short),
                        description = StringSource("MLC engine not loaded")
                    )
                    return
                }
            }

            is MediaPipeModel -> {
                if (engines.llmInference == null) {
                    showErrorState(
                        title = StringSource(R.string.error_internal_error_short),
                        description = StringSource("MediaPipe engine not loaded")
                    )
                    return
                }
            }

            is OnnxModel -> {
                if (engines.simpleGenAI == null) {
                    showErrorState(
                        title = StringSource(R.string.error_internal_error_short),
                        description = StringSource("ONNX engine not loaded")
                    )
                    return
                }
            }

            else -> {
                showErrorState(
                    title = StringSource(R.string.error_internal_error_short),
                    description = StringSource("Not selected engine or model")
                )
                return
            }
        }
        hideErrorState()
        _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.IN_PROCESS)
        messagesMap[UUID.randomUUID()] = ChatMessageUserUi(
            id = UUID.randomUUID(),
            timeStamp = System.currentTimeMillis(),
            message = message,
        )
        _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
        when (selected) {
            is MLCEngineModel -> sendMessageMLC(message)
            is MediaPipeModel -> sendMessageMediaPipe(message)
            is OnnxModel -> sendMessageOnnx(message)
            else -> {}
        }
    }

    private fun sendMessageMLC(message: String) {
        logDebug("sendMessageMLC message: $message", TAG)
        val messageID = UUID.randomUUID()
        sendMessageMLCEngineUseCase.invoke(
            message = message,
            dialogID = dialogID,
            messageID = messageID,
            mlcEngine = engines.mlcEngine!!
        ).onEach { result ->
            result.onLoading { model, _ ->
                model?.let {
                    messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                    _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                }
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.IN_PROCESS)
            }.onSuccess { model, _, _ ->
                messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
            }.onFailure { error, title, message, responseCode, errorType ->
                messagesMap[messageID] = ChatMessageErrorUi(
                    id = messageID,
                    message = message ?: "Unknown error",
                    timeStamp = System.currentTimeMillis(),
                )
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
            }
        }.launchInScope(viewModelScope)
    }

    private fun sendMessageMediaPipe(message: String) {
        logDebug("sendMessageMediaPipe message: $message", TAG)
        val messageID = UUID.randomUUID()
        sendMessageMediaPipeUseCase.invoke(
            message = message,
            dialogID = dialogID,
            messageID = messageID,
            llmInference = engines.llmInference!!
        ).onEach { result ->
            result.onLoading { model, _ ->
                model?.let {
                    messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                    _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                }
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.IN_PROCESS)
            }.onSuccess { model, _, _ ->
                messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
            }.onFailure { error, title, message, responseCode, errorType ->
                messagesMap[messageID] = ChatMessageErrorUi(
                    id = messageID,
                    message = message ?: "Unknown error",
                    timeStamp = System.currentTimeMillis(),
                )
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
            }
        }.launchInScope(viewModelScope)
    }

    private fun sendMessageOnnx(message: String) {
        logDebug("sendMessageOnnx message: $message", TAG)
        val messageID = UUID.randomUUID()
        sendMessageOnnxEngineUseCase.invoke(
            message = message,
            dialogID = dialogID,
            messageID = messageID,
            engine = engines.simpleGenAI!!
        ).onEach { result ->
            result.onLoading { model, _ ->
                model?.let {
                    messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                    _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                }
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.IN_PROCESS)
            }.onSuccess { model, _, _ ->
                messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
            }.onFailure { error, title, message, responseCode, errorType ->
                messagesMap[messageID] = ChatMessageErrorUi(
                    id = messageID,
                    message = message ?: "Unknown error",
                    timeStamp = System.currentTimeMillis(),
                )
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
            }
        }.launchInScope(viewModelScope)
    }

    fun cancelGeneration() {
        logDebug("cancelGeneration", TAG)
        sendMessageMLCEngineUseCase.cancel()
        sendMessageMediaPipeUseCase.cancel()
        _engineGeneratingStateLiveData.setValueOnMainThread(EngineGeneratingState.READY)
    }

    fun onLoadClicked() {
        engines.llmInference = null
        engines.mlcEngine = null
        if (engineLoadingState == EngineLoadingState.NOT_LOADED) {
            when (selected) {
                is MLCEngineModel -> startMLCEngine(selected as MLCEngineModel)
                is MediaPipeModel -> startMediaPipeEngine(selected as MediaPipeModel)
                is OnnxModel -> startOnnxEngine(selected as OnnxModel)
                else -> {}
            }
        } else {
            updateEngineLoadingState(EngineLoadingState.NOT_LOADED)
        }
    }

    private fun startMLCEngine(modelEngine: MLCEngineModel) {
        logDebug("Start MLC engine", TAG)
        updateEngineLoadingState(EngineLoadingState.LOADING)
        hideErrorState()
        val applicationInfo = preferences.readApplicationInfo()
        preferences.saveApplicationInfo(
            applicationInfo.copy(
                selectedModelPosition = models.indexOf(modelEngine),
            )
        )
        val modelFile = File(
            currentContext.get().filesDir,
            modelEngine.modelFileName
        )
        if (!modelFile.exists()) {
            showErrorState(
                title = StringSource(R.string.error_internal_error_short),
                description = StringSource("MLC engine error: file ${modelFile.path} not exist")
            )
            return
        }
        startMLCEngineUseCase.invoke(
            modelFile = modelFile,
            modelLib = modelEngine.modelLib,
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                if (result.model == null) {
                    showErrorState(
                        title = StringSource(R.string.error_internal_error_short),
                        description = StringSource("MLC engine error: result is null")
                    )
                    return@onSuccess
                }
                engines.mlcEngine = result.model
                _engineLiveData.setValueOnMainThread(modelEngine.engineName)
                _modelLiveData.setValueOnMainThread(modelEngine.modelName)
                updateEngineLoadingState(EngineLoadingState.LOADED)
                logDebug("MLC engine loaded", TAG)
            }.onFailure { error, title, message, responseCode, errorType ->
                showErrorState(
                    title = StringSource(R.string.error_internal_error_short),
                    description = StringSource("MLC engine error: $message")
                )
            }
        }.launchInScope(viewModelScope)
    }

    private fun startMediaPipeEngine(modelEngine: MediaPipeModel) {
        logDebug("Start MediaPipe engine", TAG)
        updateEngineLoadingState(EngineLoadingState.LOADING)
        hideErrorState()
        val applicationInfo = preferences.readApplicationInfo()
        preferences.saveApplicationInfo(
            applicationInfo.copy(
                selectedModelPosition = models.indexOf(modelEngine),
            )
        )
        val modelFile = File(
            currentContext.get().filesDir,
            modelEngine.modelFileName
        )
        if (!modelFile.exists()) {
            showErrorState(
                title = StringSource(R.string.error_internal_error_short),
                description = StringSource("MediaPipe engine error: file ${modelFile.path} not exist")
            )
            return
        }
        startEngineMediaPipeUseCase.invoke(
            modelFile = modelFile,
            context = currentContext.get(),
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                if (result.model == null) {
                    showErrorState(
                        title = StringSource(R.string.error_internal_error_short),
                        description = StringSource("MediaPipe engine error: result.model is null")
                    )
                    return@onSuccess
                }
                engines.llmInference = result.model
                _engineLiveData.setValueOnMainThread(modelEngine.engineName)
                _modelLiveData.setValueOnMainThread(modelEngine.modelName)
                updateEngineLoadingState(EngineLoadingState.LOADED)
                logDebug("MediaPipe engine loaded", TAG)
            }.onFailure { error, title, message, responseCode, errorType ->
                showErrorState(
                    title = StringSource(R.string.error_internal_error_short),
                    description = StringSource("MediaPipe engine error: $message")
                )
            }
        }.launchInScope(viewModelScope)
    }

    private fun startOnnxEngine(modelEngine: OnnxModel) {
        logDebug("Start ONNX engine", TAG)
        updateEngineLoadingState(EngineLoadingState.LOADING)
        hideErrorState()
        val applicationInfo = preferences.readApplicationInfo()
        preferences.saveApplicationInfo(
            applicationInfo.copy(
                selectedModelPosition = models.indexOf(modelEngine),
            )
        )
        val modelFile = File(
            currentContext.get().filesDir,
            modelEngine.modelFileName
        )
        if (!modelFile.exists()) {
            showErrorState(
                title = StringSource(R.string.error_internal_error_short),
                description = StringSource("ONNX engine error: file ${modelFile.path} not exist")
            )
            return
        }
        startOnnxEngineUseCase.invoke(
            modelFile = modelFile,
        ).onEach { result ->
            result.onSuccess { model, _, responseCode ->
                if (result.model == null) {
                    showErrorState(
                        title = StringSource(R.string.error_internal_error_short),
                        description = StringSource("ONNX engine error: result.model is null")
                    )
                    return@onSuccess
                }
                engines.simpleGenAI = result.model
                _engineLiveData.setValueOnMainThread(modelEngine.engineName)
                _modelLiveData.setValueOnMainThread(modelEngine.modelName)
                updateEngineLoadingState(EngineLoadingState.LOADED)
                logDebug("ONNX engine loaded", TAG)
            }.onFailure { error, title, message, responseCode, errorType ->
                showErrorState(
                    title = StringSource(R.string.error_internal_error_short),
                    description = StringSource("ONNX engine error: $message")
                )
            }
        }.launchInScope(viewModelScope)
    }

    fun onSpinnerEngineClicked() {
        logDebug("onSpinnerEngineClicked", TAG)
        updateEngineLoadingState(EngineLoadingState.NOT_LOADED)
        selected = if (selected == null) {
            models.first()
        } else {
            models.nextOrFirstOrCurrent(selected)
        }
        _engineLiveData.setValueOnMainThread(selected?.engineName ?: "Unknown engine")
        _modelLiveData.setValueOnMainThread(selected?.modelName ?: "Unknown model")
    }

    fun onSpinnerModelClicked() {
        logDebug("onSpinnerModelClicked", TAG)
        updateEngineLoadingState(EngineLoadingState.NOT_LOADED)
        selected = if (selected == null) {
            models.first()
        } else {
            val nextSelected = models.firstOrNull {
                it::class == selected!!::class && it != selected
            }
            if (nextSelected != null) {
                nextSelected
            } else {
                selected
            }
        }
        _engineLiveData.setValueOnMainThread(selected?.engineName ?: "Unknown engine")
        _modelLiveData.setValueOnMainThread(selected?.modelName ?: "Unknown model")
    }

    private fun updateEngineLoadingState(loadingState: EngineLoadingState) {
        engineLoadingState = loadingState
        _engineLoadStateLiveData.setValueOnMainThread(loadingState)
    }

}