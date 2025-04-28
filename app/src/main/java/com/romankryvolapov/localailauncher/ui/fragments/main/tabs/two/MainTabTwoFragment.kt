/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.two

import com.romankryvolapov.localailauncher.databinding.FragmentMainTabTwoBinding
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

class MainTabTwoFragment :
    BaseMainTabFragment<FragmentMainTabTwoBinding, MainTabTwoViewModel>() {

    companion object {
        private const val TAG = "MainTabTwoFragmentTag"
    }

    override fun getViewBinding() = FragmentMainTabTwoBinding.inflate(layoutInflater)
    override val viewModel: MainTabTwoViewModel by viewModel()

    override fun setupControls() {

    }

    override fun onAlertDialogResult(result: AlertDialogResult) {
        if (result.messageId == DIALOG_EXIT && result.isPositive) {
            exitProcess(0)
        }
    }

}