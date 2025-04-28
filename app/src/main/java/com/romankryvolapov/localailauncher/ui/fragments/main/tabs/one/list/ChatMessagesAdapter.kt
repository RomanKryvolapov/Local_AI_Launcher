/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list

import com.hannesdorfmann.adapterdelegates4.paging.PagedListDelegationAdapter
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUi
import com.romankryvolapov.localailauncher.utils.DefaultDiffUtilCallback
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class ChatMessagesAdapter :
    PagedListDelegationAdapter<ChatMessageUi>(DefaultDiffUtilCallback()),
    KoinComponent {

    companion object {
        private const val TAG = "ChatMessagesAdapterTag"
    }

    private val applicationsDelegate: ChatMessagesDelegate by inject()

    var clickListener: ClickListener? = null
        set(value) {
            field = value
            applicationsDelegate.openClickListener = {
                clickListener?.onOpenClicked(it)
            }
        }

    init {
        delegatesManager.apply {
            addDelegate(applicationsDelegate)
        }
    }

    fun interface ClickListener {
        fun onOpenClicked(model: ChatMessageUi)
    }

}