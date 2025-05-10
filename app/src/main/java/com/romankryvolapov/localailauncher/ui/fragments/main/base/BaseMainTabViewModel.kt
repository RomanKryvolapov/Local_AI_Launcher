/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.base

import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.models.common.DialogMessage
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import com.romankryvolapov.localailauncher.ui.fragments.base.BaseFragment.Companion.DIALOG_EXIT

abstract class BaseMainTabViewModel : BaseViewModel() {

    companion object {
        private const val TAG = "BaseMainTabViewModelTag"
    }

    final override fun onBackPressed() {
        logDebug("onBackPressed", TAG)
        showMessage(
            DialogMessage(
                messageID = DIALOG_EXIT,
                message = StringSource("Do you want to close application?"),
                title = StringSource(R.string.information),
                positiveButtonText = StringSource(R.string.yes),
                negativeButtonText = StringSource(R.string.no),
            )
        )
    }

}