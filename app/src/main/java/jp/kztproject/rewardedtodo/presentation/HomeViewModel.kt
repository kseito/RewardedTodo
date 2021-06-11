package jp.kztproject.rewardedtodo.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val prefsWrapper: PrefsWrapper
) : ViewModel() {

    private lateinit var callback: Callback

    fun initialize(callback: Callback) {
        this.callback = callback
    }

    fun logout() {
        prefsWrapper.userId = 0
        callback.onLogout()
    }

    interface Callback {
        fun onLogout()
    }
}