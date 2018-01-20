package kztproject.jp.splacounter.viewmodel

import javax.inject.Inject

class RewardViewModel @Inject constructor() {

    private var callback: Callback? = null

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun showRewardAdd() {
        callback?.showRewardAdd()
    }

    interface Callback {
        fun showRewardAdd()
    }
}
