/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.fragments.main.base

import androidx.viewbinding.ViewBinding
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import com.romankryvolapov.localailauncher.ui.fragments.base.BaseFragment

abstract class BaseMainTabFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB, VM>() {

    companion object {
        private const val TAG = "BaseMainTabFragmentTag"
    }

}