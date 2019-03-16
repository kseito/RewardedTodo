package kztproject.jp.splacounter.ui_common

import android.support.v4.app.Fragment

fun Fragment.replaceFragment(layoutId: Int, targetFragment: Fragment) {
    val beginTransaction = fragmentManager?.beginTransaction() ?: return
    beginTransaction.replace(layoutId, targetFragment)
            .addToBackStack(targetFragment.javaClass.canonicalName)
            .commit()
}