/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageModelUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi
import com.romankryvolapov.localailauncher.utils.DefaultDiffUtilCallback
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatMessagesAdapter :
    AsyncListDifferDelegationAdapter<ChatMessageAdapterMarker>(DefaultDiffUtilCallback()),
    KoinComponent {

    companion object {
        private const val TAG = "ChatMessagesAdapterTag"
    }

    private val chatMessagesUserDelegate: ChatMessagesUserDelegate by inject()
    private val chatMessagesModelDelegate: ChatMessagesModelDelegate by inject()
    private val chatMessagesErrorDelegate: ChatMessagesErrorDelegate by inject()

    var clickListener: ClickListener? = null
        set(value) {
            field = value
            chatMessagesUserDelegate.openClickListener = {
                clickListener?.onUserMessageClicked(it)
            }
            chatMessagesModelDelegate.openClickListener = {
                clickListener?.onModelMessageClicked(it)
            }
            chatMessagesErrorDelegate.openClickListener = {
                clickListener?.onErrorMessageClicked(it)
            }
        }

    init {
        delegatesManager.apply {
            addDelegate(chatMessagesUserDelegate)
            addDelegate(chatMessagesModelDelegate)
            addDelegate(chatMessagesErrorDelegate)
        }
    }

    interface ClickListener {
        fun onUserMessageClicked(model: ChatMessageUserUi)
        fun onModelMessageClicked(model: ChatMessageModelUi)
        fun onErrorMessageClicked(model: ChatMessageErrorUi)
    }

}