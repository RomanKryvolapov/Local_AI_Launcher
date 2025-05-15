package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.four

import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel

class MainTabFourViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabFourViewModelTag"
    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_FOUR

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)

    }

}