/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import androidx.fragment.app.Fragment

private const val TAG = "FragmentExtTag"

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> Fragment.findParentFragmentByType(clazz: Class<T>): T? {
    var parent = parentFragment
    while (parent != null && !clazz.isInstance(parent)) {
        parent = parent.parentFragment
    }
    return parent as? T
}