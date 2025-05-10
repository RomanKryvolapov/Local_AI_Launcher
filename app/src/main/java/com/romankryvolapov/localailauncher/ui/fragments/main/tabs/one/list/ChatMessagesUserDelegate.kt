/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.romankryvolapov.localailauncher.databinding.ListItemChatMessageUserBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi

class ChatMessagesUserDelegate :
    AdapterDelegate<MutableList<ChatMessageAdapterMarker>>() {

    companion object {
        private const val TAG = "ChatMessagesUserDelegateTag"
    }

    var openClickListener: ((model: ChatMessageUserUi) -> Unit)? = null

    override fun isForViewType(
        items: MutableList<ChatMessageAdapterMarker>,
        position: Int
    ): Boolean {
        return items[position] is ChatMessageUserUi
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflateBinding(ListItemChatMessageUserBinding::inflate))
    }

    override fun onBindViewHolder(
        items: MutableList<ChatMessageAdapterMarker>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position] as ChatMessageUserUi)
    }

    private inner class ViewHolder(
        private val binding: ListItemChatMessageUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatMessageUserUi) {
            logDebug("bind: $model", TAG)
            binding.tvMessage.text = model.message
        }
    }

}