/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.main

import androidx.annotation.IdRes
import com.romankryvolapov.localailauncher.R

enum class MainTabsEnum(
    @IdRes val menuID: Int,
    @IdRes val navigationID: Int,
    @IdRes val fragmentID: Int,
) {
    TAB_ONE(
        R.id.nav_main_tab_one,
        R.navigation.nav_main_tab_one,
        R.id.mainTabOneFragment,
    ),
    TAB_TWO(
        R.id.nav_main_tab_two,
        R.navigation.nav_main_tab_two,
        R.id.mainTabTwoFragment,
    ),
    TAB_THREE(
        R.id.nav_main_tab_three,
        R.navigation.nav_main_tab_three,
        R.id.mainTabThreeFragment,
    );

    companion object {
        fun findNavigationIDByMenuID(@IdRes menuId: Int): Int? {
            for (tab in entries) {
                if (tab.menuID == menuId) {
                    return tab.navigationID
                }
            }
            return null
        }
    }
}