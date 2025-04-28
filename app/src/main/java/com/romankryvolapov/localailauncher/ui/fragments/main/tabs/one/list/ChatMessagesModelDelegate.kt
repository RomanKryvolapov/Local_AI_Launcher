package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.romankryvolapov.localailauncher.databinding.ListItemChatMessageModelBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.models.chat.ChatMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.chat.ChatMessageModelUi

class ChatMessagesModelDelegate :
    AdapterDelegate<MutableList<ChatMessageAdapterMarker>>() {

    companion object {
        private const val TAG = "ChatMessagesModelDelegateTag"
    }

    var openClickListener: ((model: ChatMessageModelUi) -> Unit)? = null

    override fun isForViewType(
        items: MutableList<ChatMessageAdapterMarker>,
        position: Int
    ): Boolean {
        return items[position] is ChatMessageModelUi
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflateBinding(ListItemChatMessageModelBinding::inflate))
    }

    override fun onBindViewHolder(
        items: MutableList<ChatMessageAdapterMarker>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position] as ChatMessageModelUi)
    }

    private inner class ViewHolder(
        private val binding: ListItemChatMessageModelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatMessageModelUi) {
            logDebug("bind message: ${model.message} messageData:  ${model.messageData}", TAG)
            binding.tvMessage.text = model.message
            binding.tvData.text = model.messageData
        }
    }

}