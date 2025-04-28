/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import com.romankryvolapov.localailauncher.databinding.FragmentMainTabOneBinding
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabFragment
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list.ChatMessagesAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlin.system.exitProcess

class MainTabOneFragment : BaseMainTabFragment<FragmentMainTabOneBinding, MainTabOneViewModel>() {

    companion object {
        private const val TAG = "MainTabOneFragmentTag"
    }

    override fun getViewBinding() = FragmentMainTabOneBinding.inflate(layoutInflater)
    override val viewModel: MainTabOneViewModel by viewModel()

    private val adapter: ChatMessagesAdapter by inject()

    override fun setupControls() {

    }

    override fun onAlertDialogResult(result: AlertDialogResult) {
        if (result.messageId == DIALOG_EXIT && result.isPositive) {
            exitProcess(0)
        }
    }

}