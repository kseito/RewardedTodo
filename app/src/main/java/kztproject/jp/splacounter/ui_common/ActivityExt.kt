package kztproject.jp.splacounter.ui_common

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.replaceFragment(@IdRes id: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
            .replace(id, fragment)
            .commit()
}

fun FragmentActivity.replaceFragmentWithStack(@IdRes id: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
            .replace(id, fragment)
            .addToBackStack(fragment.javaClass.canonicalName)
            .commit()
}