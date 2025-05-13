/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.romankryvolapov.localailauncher.databinding.ListItemChatMessageErrorBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi

class ChatMessagesErrorDelegate :
    AdapterDelegate<MutableList<ChatMessageAdapterMarker>>() {

    companion object {
        private const val TAG = "ChatMessagesModelDelegateTag"
    }

    var openClickListener: ((model: ChatMessageErrorUi) -> Unit)? = null

    override fun isForViewType(
        items: MutableList<ChatMessageAdapterMarker>,
        position: Int
    ): Boolean {
        return items[position] is ChatMessageErrorUi
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflateBinding(ListItemChatMessageErrorBinding::inflate))
    }

    override fun onBindViewHolder(
        items: MutableList<ChatMessageAdapterMarker>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position] as ChatMessageErrorUi)
    }

    private inner class ViewHolder(
        private val binding: ListItemChatMessageErrorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatMessageErrorUi) {
            logDebug("bind: $model", TAG)
            binding.tvMessage.text = model.message
        }
    }

}