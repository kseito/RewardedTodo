package kztproject.jp.splacounter.viewmodel

import android.databinding.ObservableField
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.api.MiniatureGardenClient
import kztproject.jp.splacounter.database.RewardDao
import kztproject.jp.splacounter.database.model.Reward
import kztproject.jp.splacounter.preference.PrefsWrapper
import javax.inject.Inject

class RewardViewModel @Inject constructor(private val miniatureGardenClient: MiniatureGardenClient,
                                          private val rewardDao: RewardDao) {

    private lateinit var callback: RewardViewModelCallback
    var rewardList: MutableList<Reward> = mutableListOf()
    private var point: ObservableField<Int> = ObservableField()
    var isEmpty: ObservableField<Boolean> = ObservableField()

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
        Single.create<MutableList<Reward>> {
            val rewardList = mutableListOf<Reward>()
            rewardList.addAll(rewardDao.findAll())
            if (rewardList.size == 0) {
                it.onError(IllegalStateException())
            } else {
                it.onSuccess(rewardList)
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    rewardList = it
                    callback.showRewards(rewardList)
                    isEmpty.set(false)
                },
                        { isEmpty.set(true) })
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
                .subscribe({ counter -> callback.successAcquireReward(reward, counter.count) },
                        { callback.showError() })
    }

    fun removeRewardIfNeeded(reward: Reward) {
        if (!reward.needRepeat) {
            Single.create<Unit> { rewardDao.deleteReward(reward) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }

    fun selectReward(reward: Reward) {
        val position = rewardList.indexOf(reward)
        rewardList[position].isSelected = true
        callback.onRewardSelected()
    }
}

interface RewardViewModelCallback {

    fun showRewardAdd()

    fun showRewards(rewardList: MutableList<Reward>)

    fun showConfirmDialog(reward: Reward)

    fun showError()

    fun successAcquireReward(reward: Reward, point: Int)

    fun onRewardSelected()
}
