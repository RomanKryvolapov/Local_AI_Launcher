/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.start.splash.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.romankryvolapov.localailauncher.databinding.ListItemLoadingElementBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.models.splash.SplashLoadingMessageAdapterMarker
import com.romankryvolapov.localailauncher.models.splash.SplashLoadingMessageUi

class SplashLoadingMessagesDelegate  :
    AdapterDelegate<MutableList<SplashLoadingMessageAdapterMarker>>() {

    companion object {
        private const val TAG = "SplashLoadingMessagesDelegateTag"
    }

    override fun isForViewType(
        items: MutableList<SplashLoadingMessageAdapterMarker>,
        position: Int
    ): Boolean {
        return items[position] is SplashLoadingMessageUi
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflateBinding(ListItemLoadingElementBinding::inflate))
    }

    override fun onBindViewHolder(
        items: MutableList<SplashLoadingMessageAdapterMarker>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position] as SplashLoadingMessageUi)
    }

    private inner class ViewHolder(
        private val binding: ListItemLoadingElementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SplashLoadingMessageUi) {
            logDebug("bind: $model", TAG)
            binding.tvMessage.text = model.message
        }
    }


}