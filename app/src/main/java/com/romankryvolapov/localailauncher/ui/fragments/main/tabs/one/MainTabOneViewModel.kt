/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.domain.models.base.onFailure
import com.romankryvolapov.localailauncher.domain.models.base.onLoading
import com.romankryvolapov.localailauncher.domain.models.base.onSuccess
import com.romankryvolapov.localailauncher.domain.usecase.SendMessageUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.launchInScope
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.mappers.ChatMessageModelUiMapper
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.inject
import java.util.UUID

class MainTabOneViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabOneViewModelTag"
        private val dialogID = UUID.randomUUID()
    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_ONE

    private val sendMessageUseCase: SendMessageUseCase by inject()
    private val chatMessageModelUiMapper: ChatMessageModelUiMapper by inject()

    private val messagesMap = mutableMapOf<UUID, ChatMessageAdapterMarker>()

    private val _messagesLiveData = MutableLiveData<List<ChatMessageAdapterMarker>>()
    val messagesLiveData = _messagesLiveData.readOnly()

    private val _isLoadingLiveData = MutableLiveData<Boolean>(false)
    val isLoadingLiveData = _isLoadingLiveData.readOnly()

    fun sendMessage(message: String) {
        logDebug("sendMessage message: $message", TAG)
        _isLoadingLiveData.setValueOnMainThread(true)
        messagesMap[UUID.randomUUID()] = ChatMessageUserUi(
            id = UUID.randomUUID(),
            timeStamp = System.currentTimeMillis(),
            message = message,
        )
        _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
        val messageID = UUID.randomUUID()
        sendMessageUseCase.invoke(
            message = message,
            dialogID = dialogID,
            messageID = messageID,
        ).onEach { result ->
            result.onLoading { model, _ ->
                model?.let {
                    messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                    _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                }
            }.onSuccess { model, _, _ ->
                messagesMap[messageID] = chatMessageModelUiMapper.map(model)
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _isLoadingLiveData.setValueOnMainThread(false)
            }.onFailure { error, title, message, responseCode, errorType ->
                messagesMap[messageID] = ChatMessageErrorUi(
                    id = messageID,
                    message = message ?: "Unknown error",
                    timeStamp = System.currentTimeMillis(),
                )
                _messagesLiveData.setValueOnMainThread(messagesMap.values.toList())
                _isLoadingLiveData.setValueOnMainThread(false)
            }
        }.launchInScope(viewModelScope)
    }

    fun cancelGeneration() {
        sendMessageUseCase.cancel()
    }

}