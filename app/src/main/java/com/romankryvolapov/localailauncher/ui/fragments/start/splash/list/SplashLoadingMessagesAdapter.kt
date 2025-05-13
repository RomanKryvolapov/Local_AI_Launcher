package com.romankryvolapov.localailauncher.ui.fragments.start.splash.list

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.romankryvolapov.localailauncher.models.splash.SplashLoadingMessageAdapterMarker
import com.romankryvolapov.localailauncher.utils.DefaultDiffUtilCallback
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashLoadingMessagesAdapter :
    AsyncListDifferDelegationAdapter<SplashLoadingMessageAdapterMarker>(DefaultDiffUtilCallback()),
    KoinComponent {

    companion object {
        private const val TAG = "SplashLoadingMessagesAdapterTag"
    }

    private val splashLoadingMessagesDelegate: SplashLoadingMessagesDelegate by inject()

    init {
        delegatesManager.apply {
            addDelegate(splashLoadingMessagesDelegate)
        }
    }

}