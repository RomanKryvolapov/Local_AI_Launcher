package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.four

import com.romankryvolapov.localailauncher.databinding.FragmentMainTabFourBinding
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

class MainTabFourFragment :
    BaseMainTabFragment<FragmentMainTabFourBinding, MainTabFourViewModel>() {

    companion object {
        private const val TAG = "MainTabFourFragmentTag"
    }

    override fun getViewBinding() = FragmentMainTabFourBinding.inflate(layoutInflater)
    override val viewModel: MainTabFourViewModel by viewModel()

    override fun setupControls() {

    }

    override fun onAlertDialogResult(result: AlertDialogResult) {
        if (result.messageId == DIALOG_EXIT && result.isPositive) {
            exitProcess(0)
        }
    }

}