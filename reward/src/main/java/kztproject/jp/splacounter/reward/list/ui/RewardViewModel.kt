package kztproject.jp.splacounter.reward.list.ui

import android.databinding.ObservableField
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.repository.IPointRepository
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class RewardViewModel @Inject constructor(private val rewardListClient: IPointRepository,
                                          private val rewardDao: IRewardRepository,
                                          private val prefsWrapper: PrefsWrapper) {

    private lateinit var callback: RewardViewModelCallback
    var rewardList: MutableList<Reward> = mutableListOf()
    var selectedReward: Reward? = null
        set(value) {
            hasSelectReward.set(value != null)
            field = value
        }
    var hasSelectReward: ObservableField<Boolean> = ObservableField()
    var point: ObservableField<Int> = ObservableField()
    var isEmpty: ObservableField<Boolean> = ObservableField()

    fun setCallback(callback: RewardViewModelCallback) {
        this.callback = callback
    }

    fun setPoint(point: Int) {
        this.point.set(point)
    }

    fun showRewardDetail() {
        callback.showRewardDetail()
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

    fun loadPoint() {
        rewardListClient.loadPoint(prefsWrapper.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ callback.onStartLoadingPoint() })
                .doAfterTerminate({ callback.onTerminateLoadingPoint() })
                .subscribe({ point.set(it.point) },
                        { callback.onPointLoadFailed() })
    }

    fun acquireReward() {
        val selectedReward: Reward = this.selectedReward
                ?: throw NullPointerException("acquireReward() cannot call when selectedReward is null")
        if (point.get()!! >= selectedReward.consumePoint) {
            rewardListClient.consumePoint(prefsWrapper.userId, selectedReward.consumePoint)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ user -> callback.successAcquireReward(selectedReward, user.point) },
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

    fun editReward() {
        callback.onRewardEditSelected(selectedReward!!)
        selectedReward = null
    }

    fun deleteReward(reward: Reward, needCallback: Boolean) {
        Completable.create { e ->
            rewardDao.delete(reward)
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
        if (reward == selectedReward) {
            selectedReward = null
            rewardList[newPosition].isSelected = false
            callback.onRewardDeSelected(newPosition)
            return
        }

        selectedReward?.let {
            val oldPosition = rewardList.indexOf(it)
            if (oldPosition >= 0) {
                rewardList[oldPosition].isSelected = false
                callback.onRewardDeSelected(oldPosition)
            }
        }

        rewardList[newPosition].isSelected = true
        selectedReward = reward
        callback.onRewardSelected(newPosition)
    }

    fun logout() {
        prefsWrapper.userId = 0
        callback.onLogout()
    }
}

interface RewardViewModelCallback {

    fun showRewardDetail()

    fun showRewards(rewardList: MutableList<Reward>)

    fun showDeleteConfirmDialog(reward: Reward)

    fun showError()

    fun successAcquireReward(reward: Reward, point: Int)

    fun onRewardSelected(position: Int)

    fun onRewardDeSelected(position: Int)

    fun onRewardDeleted(reward: Reward)

    fun onRewardEditSelected(reward: Reward)

    fun onPointLoadFailed()

    fun onStartLoadingPoint()

    fun onTerminateLoadingPoint()

    fun onLogout()
}
