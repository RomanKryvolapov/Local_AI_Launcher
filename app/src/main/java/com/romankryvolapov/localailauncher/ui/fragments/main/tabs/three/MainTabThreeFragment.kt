/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.three

import com.romankryvolapov.localailauncher.databinding.FragmentMainTabThreeBinding
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

class MainTabThreeFragment :
    BaseMainTabFragment<FragmentMainTabThreeBinding, MainTabThreeViewModel>() {

    companion object {
        private const val TAG = "MainTabThreeFragmentTag"
    }

    override fun getViewBinding() = FragmentMainTabThreeBinding.inflate(layoutInflater)
    override val viewModel: MainTabThreeViewModel by viewModel()

    override fun setupControls() {

    }

    override fun onAlertDialogResult(result: AlertDialogResult) {
        if (result.messageId == DIALOG_EXIT && result.isPositive) {
            exitProcess(0)
        }
    }

}