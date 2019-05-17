package kztproject.jp.splacounter.reward.list.ui

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.repository.IPointRepository
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import project.seito.screen_transition.extention.addTo
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class RewardListViewModel @Inject constructor(private val rewardListClient: IPointRepository,
                                              private val rewardDao: IRewardRepository,
                                              private val prefsWrapper: PrefsWrapper) : ViewModel() {

    private lateinit var callback: RewardViewModelCallback
    var rewardList: MutableList<Reward> = mutableListOf()
    var selectedReward: Reward? = null
        set(value) {
            hasSelectReward.set(value != null)
            field = value
        }
    var hasSelectReward: ObservableField<Boolean> = ObservableField()
    var currentPoint: ObservableField<Int> = ObservableField()
    var isEmpty: ObservableField<Boolean> = ObservableField()
    private val compositeDisposable = CompositeDisposable()
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun setCallback(callback: RewardViewModelCallback) {
        this.callback = callback
    }

    fun setPoint(point: Int) {
        this.currentPoint.set(point)
    }

    fun showRewardDetail() {
        callback.showRewardDetail()
    }

    fun getRewards() {
        viewModelScope.launch {
            val newRewardList = rewardDao.findAll()
            rewardList.addAll(newRewardList)
            isEmpty.set(newRewardList.isEmpty())
            callback.showRewards(rewardList)
        }
    }

    fun loadPoint() {
        viewModelScope.launch {
            try {
                val point = rewardListClient.loadPoint(prefsWrapper.userId)
                currentPoint.set(point.point)
            } catch (e: Exception) {
                callback.onPointLoadFailed()
            }
        }
    }

    fun acquireReward() {
        val selectedReward: Reward = this.selectedReward
                ?: throw NullPointerException("acquireReward() cannot call when selectedReward is null")
        if (currentPoint.get()!! >= selectedReward.consumePoint) {
            rewardListClient.consumePoint(prefsWrapper.userId, selectedReward.consumePoint)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ user ->
                        callback.successAcquireReward(selectedReward, user.point)
                        currentPoint.set(user.point)
                    }, { callback.showError() })
                    .addTo(compositeDisposable)
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
                .subscribe {
                    if (needCallback) {
                        selectedReward = null
                        callback.onRewardDeleted(reward)
                    }
                }
                .addTo(compositeDisposable)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        viewModelScope.cancel()
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
