/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.romankryvolapov.localailauncher.databinding.ListItemDropDownMenuBinding
import com.romankryvolapov.localailauncher.extensions.inflateBinding
import com.romankryvolapov.localailauncher.extensions.setTextColorResource
import com.romankryvolapov.localailauncher.extensions.tintRes
import com.romankryvolapov.localailauncher.models.list.CommonSpinnerMenuItemUi

class CommonDropdownArrayAdapter(
    context: Context,
    private val clickListener: ((model: CommonSpinnerMenuItemUi) -> Unit)
) : ArrayAdapter<CommonSpinnerMenuItemUi>(
    context, 0, mutableListOf()
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return parent.inflateBinding(ListItemDropDownMenuBinding::inflate).run {
            val item = getItem(position)!!
            tvItemName.text = item.text.getString(context)
            tvItemName.maxLines = item.maxLines
            ivCheck.isInvisible = !item.isSelected
            divider.isVisible = position < count - 1
            if (item.iconRes != null) {
                ivIcon.isVisible = true
                ivIcon.setImageResource(item.iconRes)
            } else {
                ivIcon.isVisible = false
            }
            tvItemName.setTextColorResource(item.textColorRes)
            if (item.iconColorRes != null) {
                ivIcon.tintRes(item.iconColorRes)
            } else {
                // TODO
            }
            root.setOnClickListener {
                clickListener.invoke(item)
            }
            root
        }
    }

}