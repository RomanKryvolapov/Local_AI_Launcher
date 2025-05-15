/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.flow

import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.FragmentFlowContainerBinding
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.models.common.StartDestination
import com.romankryvolapov.localailauncher.ui.fragments.base.flow.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartFlowFragment : BaseFlowFragment<FragmentFlowContainerBinding, StartFlowViewModel>() {

    companion object {
        private const val TAG = "StartFlowFragmentTag"
    }

    override fun getViewBinding() = FragmentFlowContainerBinding.inflate(layoutInflater)

    override val viewModel: StartFlowViewModel by viewModel()

    override fun getFlowGraph() = R.navigation.nav_start

    override fun getStartDestination(): StartDestination {
        return StartDestination(R.id.splashFragment)
    }

    override fun onCreated() {
        super.onCreated()
        logDebug("onCreated", TAG)
    }

}