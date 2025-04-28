/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.flow

import com.romankryvolapov.localailauncher.ui.fragments.base.flow.BaseFlowFragment
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.FragmentFlowContainerBinding
import com.romankryvolapov.localailauncher.models.common.StartDestination
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartFlowFragment :
    BaseFlowFragment<FragmentFlowContainerBinding, StartFlowViewModel>() {

    override fun getViewBinding() = FragmentFlowContainerBinding.inflate(layoutInflater)

    override val viewModel: StartFlowViewModel by viewModel()

    override fun getFlowGraph() = R.navigation.nav_start

    override fun getStartDestination(): StartDestination {
        return viewModel.getStartDestination()
    }

}