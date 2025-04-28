/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import com.romankryvolapov.localailauncher.databinding.FragmentSplashBinding
import com.romankryvolapov.localailauncher.ui.fragments.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    companion object {
        private const val TAG = "SplashFragmentTag"
    }

    override fun getViewBinding() = FragmentSplashBinding.inflate(layoutInflater)

    override val viewModel: SplashViewModel by viewModel()

}