package jp.kztproject.rewardedtodo.reward.presentation.list

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.showDialog(message: String) {
    context?.let {
        AlertDialog.Builder(it)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
    }
}