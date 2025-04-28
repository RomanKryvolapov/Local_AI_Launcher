/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.flow

import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.ui.fragments.base.flow.BaseFlowViewModel
import com.romankryvolapov.localailauncher.models.common.StartDestination

class StartFlowViewModel : BaseFlowViewModel() {

    fun getStartDestination(): StartDestination {
        return StartDestination(R.id.splashFragment)
    }

}