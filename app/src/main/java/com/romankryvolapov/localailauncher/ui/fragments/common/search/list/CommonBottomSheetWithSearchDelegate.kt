/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.common.search.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romankryvolapov.localailauncher.databinding.ListItemBottomSheetWithSearchBinding
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.extensions.onClickThrottle
import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchAdapterMarker
import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchItemUi
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

class CommonBottomSheetWithSearchDelegate :
    AdapterDelegate<MutableList<CommonDialogWithSearchAdapterMarker>>() {

    var clickListener: ((model: CommonDialogWithSearchItemUi) -> Unit)? = null

    override fun isForViewType(
        items: MutableList<CommonDialogWithSearchAdapterMarker>,
        position: Int
    ): Boolean {
        return items[position] is CommonDialogWithSearchItemUi
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflateBinding(ListItemBottomSheetWithSearchBinding::inflate))
    }

    override fun onBindViewHolder(
        items: MutableList<CommonDialogWithSearchAdapterMarker>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position] as CommonDialogWithSearchItemUi)
    }

    private inner class ViewHolder(
        private val binding: ListItemBottomSheetWithSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CommonDialogWithSearchItemUi) {
            binding.tvTitle.text = model.text.getString(binding.root.context)
            binding.tvTitle.maxLines = model.maxLines
            binding.tvTitle.onClickThrottle {
                clickListener?.invoke(model)
            }
        }
    }
}