/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.common.search

import androidx.lifecycle.viewModelScope
import com.romankryvolapov.localailauncher.domain.extensions.readOnly
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.launchWithDispatcher
import com.romankryvolapov.localailauncher.extensions.readOnly
import com.romankryvolapov.localailauncher.extensions.setValueOnMainThread
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchItemUi
import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchUi
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import com.romankryvolapov.localailauncher.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

class CommonBottomSheetWithSearchViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "CommonBottomSheetWithSearchViewModelTag"
    }

    private var list: List<CommonDialogWithSearchItemUi>? = null

    private var currentModel: CommonDialogWithSearchUi? = null

    private var searchJob: Job? = null

    private val _adapterList = MutableStateFlow<List<CommonDialogWithSearchItemUi>>(emptyList())
    val adapterList = _adapterList.readOnly()

    private val _resultModel = SingleLiveEvent<CommonDialogWithSearchUi>()
    val resultModel = _resultModel.readOnly()

    fun setModel(model: CommonDialogWithSearchUi) {
        viewModelScope.launchWithDispatcher {
            currentModel = model
            list = model.list
            _adapterList.emit(list ?: emptyList())
        }
    }

    fun onClicked(selected: CommonDialogWithSearchItemUi) {
        _resultModel.setValueOnMainThread(
            currentModel?.copy(
                selectedValue = selected
            )
        )
    }

    fun onSearch(text: String?) {
        searchJob?.cancel()
        searchJob = viewModelScope.launchWithDispatcher {
            if (text.isNullOrEmpty()) {
                _adapterList.emit(list ?: emptyList())
            } else {
                if (currentModel?.customInputEnabled == true) {
                    val filtered = buildList {
                        add(
                            CommonDialogWithSearchItemUi(
                                serverValue = text,
                                text = StringSource(text),
                            )
                        )
                        list?.filter {
                            it.text.getString(currentContext.get()).contains(text, true)
                        }?.let {
                            addAll(it)
                        }

                    }
                    _adapterList.emit(filtered)
                } else {
                    _adapterList.emit(list?.filter {
                        it.text.getString(currentContext.get()).contains(text, true)
                    } ?: emptyList())
                }
            }
        }
    }

    override fun onBackPressed() {
        logDebug("onBackPressed", TAG)
        popBackStack()
    }

}