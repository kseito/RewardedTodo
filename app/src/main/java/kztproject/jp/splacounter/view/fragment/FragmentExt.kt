package kztproject.jp.splacounter.view.fragment

import android.support.v4.app.Fragment

fun Fragment.replaceFragment(layoutId: Int, targetFragment: Fragment) {
    fragmentManager.beginTransaction()
            .replace(layoutId, targetFragment)
            .addToBackStack(targetFragment.javaClass.canonicalName)
            .commit()
}