/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.di

import com.romankryvolapov.localailauncher.ui.activity.MainViewModel
import com.romankryvolapov.localailauncher.ui.fragments.main.flow.MainTabsFlowViewModel
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.MainTabOneViewModel
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.three.MainTabThreeViewModel
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.two.MainTabTwoViewModel
import com.romankryvolapov.localailauncher.ui.fragments.start.flow.StartFlowViewModel
import com.romankryvolapov.localailauncher.ui.fragments.start.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {


    // Common

    viewModel<StartFlowViewModel> {
        StartFlowViewModel()
    }

    viewModel<SplashViewModel> {
        SplashViewModel()
    }

    // Main

    viewModel<MainViewModel> {
        MainViewModel()
    }

    viewModel<MainTabsFlowViewModel> {
        MainTabsFlowViewModel()
    }

    viewModel<MainTabOneViewModel> {
        MainTabOneViewModel()
    }

    viewModel<MainTabThreeViewModel> {
        MainTabThreeViewModel()
    }

    viewModel<MainTabTwoViewModel> {
        MainTabTwoViewModel()
    }

}