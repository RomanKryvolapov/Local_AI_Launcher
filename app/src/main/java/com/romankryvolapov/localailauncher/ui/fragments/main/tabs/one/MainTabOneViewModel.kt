/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import ai.mlc.mlcllm.MLCEngine
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionMessage
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionRole
import ai.mlc.mlcllm.OpenAIProtocol.StreamOptions
import android.R.id.message
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.databinding.ActivityMainBinding
import com.romankryvolapov.localailauncher.domain.repository.common.PreferencesRepository
import com.romankryvolapov.localailauncher.domain.usecase.CopyAssetsToFileUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.launchWithDispatcher
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUi
import com.romankryvolapov.localailauncher.models.common.LoadingState
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.activity.MainActivity
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.two.MainTabTwoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import java.io.File
import java.util.UUID
import kotlin.collections.mutableMapOf
import kotlin.getValue

class MainTabOneViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabsFlowViewModelTag"
        private const val MODEL_NAME = "gemma-3-1b-it-q4f16_1-MLC"
        private const val MODEL_LIB = "gemma3_text_q4f16_1_15281663a194ab7a82d5f6c3b0d59432"
    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_ONE

    private val copyAssetsToFileUseCase: CopyAssetsToFileUseCase by inject()

    private val messagesMap = mutableMapOf<UUID, ChatMessageUi>()

    private val _messagesLiveData = MutableLiveData<List<ChatMessageUi>>()
    val messagesLiveData = _messagesLiveData.readOnly()

    private var engine: MLCEngine? = null

    override fun onFirstAttach() {
        logDebug(TAG, "onFirstAttach")

        Log.d(TAG, "OpenCL available: ${findOpenCLPaths()}")

        showLoader()

        val isFirstRun = preferences.readApplicationInfo()?.isFirstFun == true

        if (isFirstRun) {
            copyAssetsToFileUseCase.invoke(
                modelName = MODEL_NAME,
                filesDir = currentContext.get().filesDir,
                assetManager = currentContext.get().assets,
            ).onEach { result ->
                if (result) {
                    Log.d(TAG, "Model copied")
                    startEngine()
                } else {
                    Log.e(TAG, "Model NOT copied")
                    hideLoader()
                    showErrorState(
                        title = StringSource("Model NOT copied"),
                        description = StringSource("Model NOT copied")
                    )
                }
            }
        } else {
            startEngine()
        }
    }

    private fun startEngine() {
        try {
            val modelDir = File(currentContext.get().filesDir, MODEL_NAME)
            val modelPath = modelDir.absolutePath
            engine = MLCEngine()
            engine!!.reload(modelPath, MODEL_LIB)
            Log.d(TAG, "engine reloaded")
            hideLoader()
        } catch (e: Exception) {
            Log.e(TAG, "Chat error", e)
            hideLoader()
            showErrorState(
                title = StringSource("Error"),
                description = StringSource("Error: ${e.message}")
            )
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launchWithDispatcher {
            showLoader()

            messagesMap[UUID.randomUUID()] = ChatMessageUi(
                id = UUID.randomUUID(),
                timeStamp = System.currentTimeMillis(),
                isUserMessage = true,
                message = message,
                messageData = ""
            )

            val assistantMessageID = UUID.randomUUID()

            messagesMap[assistantMessageID] = ChatMessageUi(
                id = assistantMessageID,
                timeStamp = System.currentTimeMillis(),
                isUserMessage = false,
                message = message,
                messageData = ""
            )

            try {

                val streamingResponse = StringBuilder()

                val channel = engine!!.chat.completions.create(
                    messages = listOf(
                        ChatCompletionMessage(
                            role = ChatCompletionRole.user,
                            content = buildGemmaPrompt("Hello")
                        )
                    ),
                    stream_options = StreamOptions(include_usage = true),
                    stop = listOf("<end_of_turn>")
                )
                for (response in channel) {
                    response.choices.firstOrNull()?.delta?.content?.asText()?.let { textChunk ->
                        streamingResponse.append(textChunk)
                    }
                    response.usage?.extra?.asTextLabel()?.let { usageInfo ->
                        Log.d(TAG, "Usage: $usageInfo")
                    }
                }

                messagesMap[assistantMessageID] = ChatMessageUi(
                    id = assistantMessageID,
                    timeStamp = System.currentTimeMillis(),
                    isUserMessage = false,
                    message = streamingResponse.toString(),
                    messageData = ""
                )

                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())

                hideLoader()

            } catch (e: Exception) {
                Log.e(TAG, "Chat error", e)
                hideLoader()
                showErrorState(
                    title = StringSource("Error"),
                    description = StringSource("Error: ${e.message}")
                )
            }
        }
    }

    fun buildGemmaPrompt(userMessage: String): String {
        return """
        <bos><start_of_turn>user
        $userMessage
        <end_of_turn>
        <start_of_turn>model
    """.trimIndent()
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

    fun copyAssetsToFiles(assetFolderName: String) {
        val assetManager = currentContext.get().assets
        val filesDir = File(currentContext.get().filesDir, assetFolderName)
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        assetManager.list(assetFolderName)?.forEach { filename ->
            val inStream = assetManager.open("$assetFolderName/$filename")
            val outFile = File(filesDir, filename)
            val outStream = outFile.outputStream()
            inStream.copyTo(outStream)
            inStream.close()
            outStream.close()
        }
    }

}