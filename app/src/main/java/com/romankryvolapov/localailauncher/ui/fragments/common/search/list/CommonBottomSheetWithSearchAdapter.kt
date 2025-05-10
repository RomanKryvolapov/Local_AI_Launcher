/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.common.search.list

import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchAdapterMarker
import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchItemUi
import com.romankryvolapov.localailauncher.utils.DefaultDiffUtilCallback
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CommonBottomSheetWithSearchAdapter :
    AsyncListDifferDelegationAdapter<CommonDialogWithSearchAdapterMarker>(DefaultDiffUtilCallback()),
    KoinComponent {

    private val commonBottomSheetWithSearchDelegate: CommonBottomSheetWithSearchDelegate by inject()

    var clickListener: ClickListener? = null
        set(value) {
            field = value
            commonBottomSheetWithSearchDelegate.clickListener = { model ->
                clickListener?.onClicked(model)
            }
        }

    init {
        items = mutableListOf()
        @Suppress("UNCHECKED_CAST")
        delegatesManager.apply {
            addDelegate(commonBottomSheetWithSearchDelegate as AdapterDelegate<MutableList<CommonDialogWithSearchAdapterMarker>>)
        }
    }

    fun interface ClickListener {
        fun onClicked(selected: CommonDialogWithSearchItemUi)
    }

}