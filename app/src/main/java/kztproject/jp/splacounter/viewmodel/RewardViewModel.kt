package kztproject.jp.splacounter.viewmodel

import android.databinding.ObservableField
import io.reactivex.Completable
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
    var selectedReward: Reward? = null
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

    fun acquireReward() {
        val selectedReward: Reward = this.selectedReward
                ?: throw NullPointerException("acquireReward() cannot call when selectedReward is null")
        if (point.get() >= selectedReward.consumePoint) {
            miniatureGardenClient.consumeCounter(PrefsWrapper.userId, selectedReward.consumePoint)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ counter -> callback.successAcquireReward(selectedReward, counter.count) },
                            { callback.showError() })
        } else {
            callback.showError()
        }
    }

    fun confirmDelete() {
        callback.showDeleteConfirmDialog(selectedReward!!)
    }

    fun deleteRewardIfNeeded(reward: Reward) {
        if (reward.needRepeat) {
            return
        }

        deleteReward(reward, false)
    }

    fun deleteReward(reward: Reward, needCallback: Boolean) {
        Completable.create { e ->
            rewardDao.deleteReward(reward)
            e.onComplete()
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (needCallback) {
                        selectedReward = null
                        callback.onRewardDeleted(reward)
                    }
                })
    }

    fun switchReward(reward: Reward) {
        val newPosition = rewardList.indexOf(reward)
        if (selectedReward == reward) {
            rewardList[newPosition].isSelected = false
            selectedReward = null
            callback.onRewardDeSelected(newPosition)
        } else if (selectedReward != null && selectedReward != reward) {
            rewardList[newPosition].isSelected = true
            val oldPosition = rewardList.indexOf(selectedReward!!)
            rewardList[oldPosition].isSelected = false
            selectedReward = reward
            callback.onRewardDeSelected(oldPosition)
            callback.onRewardSelected(newPosition)
        } else {
            rewardList[newPosition].isSelected = true
            selectedReward = reward
            callback.onRewardSelected(newPosition)
        }
    }
}

interface RewardViewModelCallback {

    fun showRewardAdd()

    fun showRewards(rewardList: MutableList<Reward>)

    fun showDeleteConfirmDialog(reward: Reward)

    fun showError()

    fun successAcquireReward(reward: Reward, point: Int)

    fun onRewardSelected(position: Int)

    fun onRewardDeSelected(position: Int)

    fun onRewardDeleted(reward: Reward)
}
