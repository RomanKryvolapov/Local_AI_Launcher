/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.flow

import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.ui.fragments.base.flow.BaseFlowViewModel

class StartFlowViewModel : BaseFlowViewModel() {

    companion object {
        private const val TAG = "StartFlowViewModelTag"
    }

    override fun onFirstAttach() {
        logDebug(TAG, "onFirstAttach")
    }

}