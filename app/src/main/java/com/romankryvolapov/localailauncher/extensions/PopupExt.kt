/**
 * Creates and shows a list of [items] in drop down popup window.
 * [dropDownAnimationAnchor] used to calculate an drop down animation start point in the screen.
 * [dropDownInputAnchor] used to calculate the max width of drop down which is cannot be more
 * than [dropDownInputAnchor].
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListAdapter
import androidx.appcompat.widget.ListPopupWindow
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.models.list.CommonSpinnerMenuItemUi
import com.romankryvolapov.localailauncher.models.list.CommonSpinnerUi
import com.romankryvolapov.localailauncher.ui.view.CommonDropdownArrayAdapter
import kotlin.math.max

fun View.showSpinner(
    model: CommonSpinnerUi,
    clickListener: ((model: CommonSpinnerMenuItemUi) -> Unit)
) = ListPopupWindow(
        context,
        null,
        0,
        R.style.CustomListPopupWindowStyle
    ).apply {
        val adapter = CommonDropdownArrayAdapter(
            context = context,
            clickListener = {
                dismiss()
                clickListener.invoke(it)
            }
        )
        anchorView = this@showSpinner
        setDropDownGravity(Gravity.START)
        setAdapter(adapter)
        adapter.swapItems(model.list)
        height = ListPopupWindow.WRAP_CONTENT
        isModal = true
        val width = measureContentWidth(context, adapter)
        if (width > this@showSpinner.width) {
            this.width = width
        }
        setOnDismissListener {
            setAdapter(null)
        }
        show()
    }


fun <T> showListPopupDropDownWindow(
    context: Context,
    anchorView: View,
    adapter: ArrayAdapter<T>,
    items: List<T>,
    onItemClickListener: (T) -> Unit
): ListPopupWindow {
    return ListPopupWindow(
        context,
        null,
        0,
        R.style.CustomListPopupWindowStyle
    ).apply {
        this.anchorView = anchorView
        setDropDownGravity(Gravity.START)
        setAdapter(adapter)
        adapter.swapItems(items)
        setOnItemClickListener { _, _, position, _ ->
            onItemClickListener.invoke(items[position])
            dismiss()
        }
        height = ListPopupWindow.WRAP_CONTENT
        isModal = true
        val width = measureContentWidth(context, adapter)
        if (width > anchorView.width) {
            this.width = width
        }
        show()
    }
}

private fun measureContentWidth(context: Context, adapter: ListAdapter): Int {
    val measureParentViewGroup = FrameLayout(context)
    var itemView: View? = null
    var maxWidth = 0
    var itemType = 0
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    for (index in 0 until adapter.count) {
        val positionType = adapter.getItemViewType(index)
        if (positionType != itemType) {
            itemType = positionType
            itemView = null
        }
        itemView = adapter.getView(index, itemView, measureParentViewGroup)
        itemView.measure(widthMeasureSpec, heightMeasureSpec)
        val itemWidth = itemView.measuredWidth
        if (itemWidth > maxWidth) {
            maxWidth = itemWidth
        }
    }
    return maxWidth
}

//fun <T> showListPopupDropDownWindow(
//    context: Context,
//    dropDownAnimationAnchor: View,
//    dropDownInputAnchor: View,
//    dropDownArrayAdapter: ArrayAdapter<T>,
//    items: List<T>,
//    onItemClickListener: (T) -> Unit
//): ListPopupWindow {
//    val window = ListPopupWindow(context, null, 0, R.style.CustomListPopupWindowStyle).apply {
//        anchorView = dropDownAnimationAnchor
//        setDropDownGravity(Gravity.START)
//        setAdapter(dropDownArrayAdapter)
//        dropDownArrayAdapter.swapItems(items)
//        setOnItemClickListener { _, _, position, _ ->
//            onItemClickListener.invoke(items[position])
//            dismiss()
//        }
//    }
//    val screenMetrics = context.resources.displayMetrics
//    val screenWidth = screenMetrics.widthPixels
//    val anchorOnScreen = IntArray(2)
//    dropDownAnimationAnchor.getLocationOnScreen(anchorOnScreen)
//    val anchorRight = anchorOnScreen[0] + dropDownAnimationAnchor.width
//    if (anchorRight > screenWidth / 2) {
//        val windowWidth = 200
//        window.width = windowWidth
//        window.horizontalOffset = -windowWidth
//    }
//
//    window.show()
//    return window
//}

/**
 * Measures all items in the list and apply the max width as the width
 * of popup menu. The max width cannot be more than [anchor] measuredWidth.
 * This method works only for single item view type in [listAdapter].
 */
private fun ListPopupWindow.measureAndApplyMaxWidth(
    anchor: View, listAdapter:
    ArrayAdapter<*>
) {
    val maxWidth = measureMaxWidth(anchor, listAdapter)
    width = if (maxWidth >= anchor.measuredWidth) {
        anchor.measuredWidth
    } else {
        maxWidth
    }
}

private fun measureMaxWidth(anchor: View, listAdapter: ArrayAdapter<*>): Int {
    var maxWidth = 0
    var itemView: View? = null
    val measureTempLayout = FrameLayout(anchor.context)
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    for (position in 0 until listAdapter.count) {
        measureTempLayout.removeAllViews()
        itemView = listAdapter.getView(position, itemView, measureTempLayout)
        itemView.measure(widthMeasureSpec, heightMeasureSpec)
        maxWidth = max(maxWidth, itemView.measuredWidth)
    }
    return maxWidth
}

fun <T> ArrayAdapter<T>.swapItems(list: List<T>) {
    clear()
    addAll(list)
    notifyDataSetChanged()
}