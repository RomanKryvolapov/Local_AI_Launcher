/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.flow

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.FragmentMainTabsFlowContainerBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.models.common.BannerMessage
import com.romankryvolapov.localailauncher.models.main.MainTabsEnum
import com.romankryvolapov.localailauncher.ui.fragments.base.flow.BaseFlowFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.ref.WeakReference

class MainTabsFlowFragment :
    BaseFlowFragment<FragmentMainTabsFlowContainerBinding, MainTabsFlowViewModel>() {

    companion object {
        private const val TAG = "MainTabsFlowFragmentTag"
    }

    override fun getViewBinding() = FragmentMainTabsFlowContainerBinding.inflate(layoutInflater)

    override val viewModel: MainTabsFlowViewModel by viewModel()
    private val args: MainTabsFlowFragmentArgs by navArgs()

    val navHostFragmentMap = mutableMapOf<Int, WeakReference<NavHostFragment>>()

    override fun setupControls() {
        logDebug("setupControls", TAG)
        if (navHostFragmentMap.values.isEmpty()) {
            setupBottomNavigation()
        }
    }

    private fun setupBottomNavigation() {
        logDebug("setupBottomNavigation", TAG)
        try {
            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                val selectedGraphId = MainTabsEnum.findNavigationIDByMenuID(item.itemId)
                return@setOnItemSelectedListener if (selectedGraphId != null) {
                    if (!navHostFragmentMap.containsKey(item.itemId)) {
                        logDebug("navHostFragmentMap add fragment", TAG)
                        val navHostFragment = NavHostFragment.create(selectedGraphId)
                        navHostFragmentMap[item.itemId] = WeakReference(navHostFragment)
                        childFragmentManager.beginTransaction()
                            .add(
                                R.id.flowTabsNavigationContainer,
                                navHostFragment,
                                item.itemId.toString()
                            )
                            .commitNow()
                    }
                    navHostFragmentMap.forEach { (itemId, weakRefNavHostFragment) ->
                        val fragment = weakRefNavHostFragment.get()
                        if (fragment != null) {
                            childFragmentManager.beginTransaction().apply {
                                if (itemId == item.itemId) {
                                    show(fragment)
                                } else {
                                    hide(fragment)
                                }
                                commit()
                            }
                        }
                    }
                    true
                } else false
            }
            binding.bottomNavigationView.selectedItemId = args.openOnTab
        } catch (e: Exception) {
            logError("parseArguments Exception: ${e.message}", e, TAG)
            showMessage(BannerMessage.error(R.string.error_internal_error_short))
        }
    }

}