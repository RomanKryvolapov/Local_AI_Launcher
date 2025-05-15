/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.three

import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel

class MainTabThreeViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabThreeViewModelTag"
    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_THREE

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)

    }

}