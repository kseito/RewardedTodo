package kztproject.jp.splacounter.viewmodel

import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.api.MiniatureGardenClient
import kztproject.jp.splacounter.model.Reward
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Inject

class RewardViewModel @Inject constructor(private val miniatureGardenClient: MiniatureGardenClient) {

    private lateinit var callback: RewardViewModelCallback
    private var point: ObservableField<Int> = ObservableField()

    fun setCallback(callback: RewardViewModelCallback) {
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

    fun canAcquireReward(reward: Reward) {
        if (point.get() >= reward.consumePoint) {
            callback.showConfirmDialog(reward)
        } else {
            callback.showError()
        }
    }

    fun acquireReward(reward: Reward) {
        miniatureGardenClient.consumeCounter(PrefsWrapper.userId, reward.consumePoint)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ counter -> callback.successAcquireReward(counter.count)})
    }

}

interface RewardViewModelCallback {

    fun showRewardAdd()

    fun showRewards(rewardList: List<Reward>)

    fun showConfirmDialog(reward: Reward)

    fun showError()

    fun successAcquireReward(point: Int)
}
