/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.two

import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabViewModel

class MainTabTwoViewModel : BaseMainTabViewModel() {

    companion object {
        private const val TAG = "MainTabTwoViewModelTag"
    }

    override var mainTabsEnum: MainTabsEnum? = MainTabsEnum.TAB_TWO

    override fun onFirstAttach() {
        logDebug("onFirstAttach", TAG)

    }

}