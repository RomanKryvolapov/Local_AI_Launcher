/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import ai.mlc.mlcllm.MLCEngine
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionMessage
import ai.mlc.mlcllm.OpenAIProtocol.ChatCompletionRole
import ai.mlc.mlcllm.OpenAIProtocol.StreamOptions
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.extensions.launchWithDispatcher
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageModelUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel
import kotlinx.coroutines.Job
import org.koin.core.component.inject
import java.util.UUID

class MainTabOneViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabOneViewModelTag"

    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_ONE

    private val engine: MLCEngine by inject()

    private val _messagesLiveData = MutableLiveData<List<ChatMessageAdapterMarker>>()
    val messagesLiveData = _messagesLiveData.readOnly()

    private val _isLoadingLiveData = MutableLiveData<Boolean>(false)
    val isLoadingLiveData = _isLoadingLiveData.readOnly()

    private var generationJob: Job? = null

    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
    }

    private val messagesMap = mutableMapOf<UUID, ChatMessageAdapterMarker>()

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)
    }


    fun sendMessage(message: String) {

        logDebug("sendMessage message: $message", TAG)

        generationJob?.cancel()
        generationJob = viewModelScope.launchWithDispatcher {

            _isLoadingLiveData.setValueOnMainThread(true)

            messagesMap[UUID.randomUUID()] = ChatMessageUserUi(
                id = UUID.randomUUID(),
                timeStamp = System.currentTimeMillis(),
                message = message,
            )

            _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())

            val modelMessageID = UUID.randomUUID()

            messagesMap[modelMessageID] = ChatMessageModelUi(
                id = modelMessageID,
                timeStamp = System.currentTimeMillis(),
                message = message,
                messageData = ""
            )

            try {

                val streamingResponse = StringBuilder()
                var usageInfoText = StringBuilder()

                val channel = engine.chat.completions.create(
                    messages = listOf(
                        ChatCompletionMessage(
                            role = ChatCompletionRole.user,
                            content = buildGemmaPrompt(message)
                        )
                    ),
                    stream_options = StreamOptions(include_usage = true),
                    stop = listOf("<end_of_turn>")
                )

                for (response in channel) {
                    response.choices.firstOrNull()?.delta?.content?.asText()?.let { textChunk ->
                        streamingResponse.append(textChunk)
                    }
                    response.usage?.let { usage ->
                        if (usageInfoText.isEmpty()) {
                            usageInfoText.append("prompt: ${usage.prompt_tokens} tok ")
                            usageInfoText.append("completion: ${usage.completion_tokens} tok ")
                            usageInfoText.append("total: ${usage.total_tokens} tok ")
                            usageInfoText.append("\n")
                            usageInfoText.append("prefill: ${String.format("%.1f", usage.extra?.prefill_tokens_per_s)} tok/s ")
                            usageInfoText.append("decode: ${String.format("%.1f", usage.extra?.decode_tokens_per_s)} tok/s ")
                        }
                    }
                    messagesMap[modelMessageID] = ChatMessageModelUi(
                        id = modelMessageID,
                        timeStamp = System.currentTimeMillis(),
                        message = streamingResponse.toString(),
                        messageData = usageInfoText.toString()
                    )
                    _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                }

                messagesMap[modelMessageID] = ChatMessageModelUi(
                    id = modelMessageID,
                    timeStamp = System.currentTimeMillis(),
                    message = streamingResponse.toString(),
                    messageData = usageInfoText.toString()
                )
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())

                _isLoadingLiveData.setValueOnMainThread(false)

            } catch (e: Exception) {
                logError("Error", e, TAG)

                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())

                messagesMap[modelMessageID] = ChatMessageErrorUi(
                    id = modelMessageID,
                    timeStamp = System.currentTimeMillis(),
                    message = e.message.toString(),
                )

                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())

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

}