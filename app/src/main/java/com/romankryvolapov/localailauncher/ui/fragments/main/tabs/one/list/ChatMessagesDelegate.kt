/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsFallbackAdapterDelegate
import com.romankryvolapov.localailauncher.databinding.ListItemChatMessageBinding
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUi

class ChatMessagesDelegate :
    AbsFallbackAdapterDelegate<MutableList<ChatMessageUi>>() {

    companion object {
        private const val TAG = "ChatMessagesDelegateTag"
    }

    var openClickListener: ((model: ChatMessageUi) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflateBinding(ListItemChatMessageBinding::inflate))
    }

    override fun onBindViewHolder(
        items: MutableList<ChatMessageUi>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position])
    }

    private inner class ViewHolder(
        private val binding: ListItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatMessageUi) {

        }
    }

}