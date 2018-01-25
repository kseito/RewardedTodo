package kztproject.jp.splacounter.viewmodel

import android.databinding.ObservableField
import kztproject.jp.splacounter.model.Reward
import javax.inject.Inject

class RewardViewModel @Inject constructor() {

    private lateinit var callback: Callback
    private var point: ObservableField<Int> = ObservableField()

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setPoint(point: Int) {
        this.point.set(point)
    }

    fun showRewardAdd() {
        callback.showRewardAdd()
    }

    fun getRewards() {
        val rewardList = arrayListOf<Reward>()
        rewardList.add(Reward(1, "test1", 12, "test1 description", "https://github.com"))
        rewardList.add(Reward(2, "test2", 8, "test2 description", "https://github.com"))
        callback.showRewards(rewardList)
    }

    interface Callback {
        fun showRewardAdd()
        fun showRewards(rewardList: List<Reward>)
        fun showConfirmDialog(reward: Reward)
        fun showErrorDialog()
    }

    fun canAcquireReward(reward: Reward) {
        if (point.get() >= reward.consumePoint) {
            callback.showConfirmDialog(reward)
        } else {
            callback.showErrorDialog()
        }
    }
}
