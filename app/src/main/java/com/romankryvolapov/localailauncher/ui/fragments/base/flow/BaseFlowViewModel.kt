/**
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.base.flow

import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.ui.BaseViewModel

abstract class BaseFlowViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "BaseFlowViewModelTag"
    }

    final override fun onBackPressed() {
        logError("onBackPressed", TAG)
        // not need
    }

}