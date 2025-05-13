/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.start.splash

import com.romankryvolapov.localailauncher.databinding.FragmentSplashBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.enableChangeAnimations
import com.romankryvolapov.localailauncher.ui.fragments.base.BaseFragment
import com.romankryvolapov.localailauncher.ui.fragments.start.splash.list.SplashLoadingMessagesAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    companion object {
        private const val TAG = "SplashFragmentTag"
    }

    override fun getViewBinding() = FragmentSplashBinding.inflate(layoutInflater)

    override val viewModel: SplashViewModel by viewModel()
    private val adapter: SplashLoadingMessagesAdapter by inject()

    override fun setupControls() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.enableChangeAnimations(false)
    }

    override fun subscribeToLiveData() {
        viewModel.messagesLiveData.observe(viewLifecycleOwner) {
            logDebug("messagesLiveData size: ${it.size}", TAG)
            adapter.items = it
            binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

}