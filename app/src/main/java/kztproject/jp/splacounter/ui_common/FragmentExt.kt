package kztproject.jp.splacounter.ui_common

import androidx.fragment.app.Fragment

fun Fragment.replaceFragment(layoutId: Int, targetFragment: Fragment) {
    val beginTransaction = fragmentManager?.beginTransaction() ?: return
    beginTransaction.replace(layoutId, targetFragment)
            .addToBackStack(targetFragment.javaClass.canonicalName)
            .commit()
}

fun Fragment.replaceFragmentWithNoStack(layoutId: Int, targetFragment: Fragment) {
    val beginTransaction = fragmentManager?.beginTransaction() ?: return
    beginTransaction.replace(layoutId, targetFragment)
            .commit()
}