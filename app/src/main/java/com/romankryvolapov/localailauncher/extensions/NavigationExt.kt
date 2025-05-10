/**
 * Search for activity navigation controller by container ID.
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "NavigationExtTag"

fun Fragment.findActivityNavController(): NavController {
    val host = requireActivity().supportFragmentManager
        .findFragmentById(R.id.navigationContainer) as NavHostFragment
    return host.navController
}

fun FragmentManager.throwBackPressedEvent(
    @IdRes containerId: Int,
): Boolean {
    val navHostFragment = (findFragmentById(containerId) as? NavHostFragment) ?: run {
        logError("fragment from containerId is not NavHostFragment", TAG)
        return false
    }
    return (navHostFragment.childFragmentManager.fragments
        .firstOrNull() as? BaseFragment<*, *>)?.let { currentFragment ->
        currentFragment.onBackPressed()
        true
    } ?: run {
        logError("fragment is not BaseFragment", TAG)
        false
    }
}

fun NavController.navigateNewRoot(
    @IdRes fragment: Int,
    bundle: Bundle? = null,
) {
    try {
        popBackStack(R.id.nav_activity, true)
        navigate(fragment, bundle)
    } catch (e: Exception) {
        logError("navigateNewRoot Exception: ${e.message}", e, TAG)
    }
}

fun NavController.navigateNewRoot(
    directions: NavDirections,
) {
    try {
        popBackStack(R.id.nav_activity, true)
        navigate(directions)
    } catch (e: Exception) {
        logError("navigateNewRoot Exception: ${e.message}", e, TAG)
    }
}

fun NavController.navigateNewRootInFlow(
    @IdRes flowGraphId: Int,
    @IdRes fragment: Int,
    bundle: Bundle? = null,
) {
    try {
        popBackStack(flowGraphId, true)
        navigate(fragment, bundle)
    } catch (e: Exception) {
        logError("navigateNewRootInFlow Exception: ${e.message}", e, TAG)
    }
}

fun NavController.navigateNewRootInFlow(
    flowGraphId: Int,
    directions: NavDirections,
) {
    try {
        popBackStack(flowGraphId, true)
        navigate(directions)
    } catch (e: Exception) {
        logError("navigateNewRootInFlow Exception: ${e.message}", e, TAG)
    }
}

/**
 * Because we have the hierarchy like this:
 * Activity -> NavFragment -> Flow Fragment -> Nav Fragment -> Base Fragment,
 * we cannot use default extensions for Result listening, they would use wrong fragment
 * manager. So we need to find correct fragment manager manually using this method.
 *
 * Only in case where you want to listen the result from Flow Fragment in Base fragment.
 * Otherwise the default extensions should be used.
 */
fun Fragment.findParentFragmentResultListenerFragmentManager(): FragmentManager? {
    return requireActivity().supportFragmentManager.fragments.firstOrNull()?.childFragmentManager
}

/**
 * Find a parent flow fragment to current base fragment according to navigation logic.
 * @see findParentFragmentResultListenerFragmentManager
 */
fun Fragment.findParentFlowFragment(): Fragment? {
    return parentFragment?.parentFragment
}

fun View.findActivityNavController(): NavController? {
    val host = (getActivityFromView(this) as? FragmentActivity)
        ?.supportFragmentManager
        ?.findFragmentById(R.id.navigationContainer) as? NavHostFragment
    return host?.navController
}

fun getActivityFromView(view: View): Activity? {
    // Gross way of unwrapping the Activity so we can get the FragmentManager
    var context: Context = view.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun NavController.navigateTo(
    directions: NavDirections,
    viewModelScope: CoroutineScope,
) {
    try {
        if (isMainThread()) {
            navigate(directions)
        } else {
            viewModelScope.launch(Dispatchers.Main) {
                navigate(directions)
            }
        }
    } catch (e: Exception) {
        logError("navigate Exception: ${e.message}", e, TAG)
    }
}

fun NavController.navigateTo(
    @IdRes fragment: Int,
    viewModelScope: CoroutineScope,
) {
    try {
        if (isMainThread()) {
            navigate(fragment)
        } else {
            viewModelScope.launch(Dispatchers.Main) {
                navigate(fragment)
            }
        }
    } catch (e: Exception) {
        logError("navigate Exception: ${e.message}", e, TAG)
    }
}

fun NavController.navigateTo(
    @IdRes fragment: Int,
    bundle: Bundle,
    viewModelScope: CoroutineScope,
) {
    try {
        if (isMainThread()) {
            navigate(fragment, bundle)
        } else {
            viewModelScope.launch(Dispatchers.Main) {
                navigate(fragment, bundle)
            }
        }
    } catch (e: Exception) {
        logError("navigate Exception: ${e.message}", e, TAG)
    }
}

fun NavController.popBackStackToFragment(
    @IdRes fragment: Int,
    viewModelScope: CoroutineScope,
) {
    try {
        if (isMainThread()) {
            val success = popBackStack(fragment, false)
            if (!success) {
                logError("popBackStackToFragment error, fragment not found", TAG)
            }
        } else {
            viewModelScope.launch(Dispatchers.Main) {
                val success = popBackStack(fragment, false)
                if (!success) {
                    logError("popBackStackToFragment error, fragment not found", TAG)
                }
            }
        }
    } catch (e: Exception) {
        logError("popBackStackToFragment Exception: ${e.message}", e, TAG)
    }
}

fun NavController.navigateNewRoot(
    directions: NavDirections,
    viewModelScope: CoroutineScope,
) {
    if (isMainThread()) {
        navigateNewRoot(
            directions = directions,
        )
    } else {
        viewModelScope.launch(Dispatchers.Main) {
            navigateNewRoot(
                directions = directions,
            )
        }
    }
}

fun NavController.navigateNewRoot(
    @IdRes fragment: Int,
    bundle: Bundle? = null,
    viewModelScope: CoroutineScope,
) {
    if (isMainThread()) {
        navigateNewRoot(
            fragment = fragment,
            bundle = bundle,
        )
    } else {
        viewModelScope.launch(Dispatchers.Main) {
            navigateNewRoot(
                fragment = fragment,
                bundle = bundle,
            )
        }
    }
}

fun NavController.isFragmentInBackStack(
    @IdRes destinationId: Int
): Boolean {
    return try {
        getBackStackEntry(destinationId = destinationId)
        true
    } catch (exception: Exception) {
        false
    }
}

fun isMainThread(): Boolean {
    return Thread.currentThread() == Looper.getMainLooper().thread
}