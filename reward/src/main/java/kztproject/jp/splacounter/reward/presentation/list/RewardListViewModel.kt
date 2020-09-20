package kztproject.jp.splacounter.reward.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kztproject.jp.splacounter.reward.application.usecase.GetPointUseCase
import kztproject.jp.splacounter.reward.application.usecase.GetRewardsUseCase
import kztproject.jp.splacounter.reward.application.usecase.LotteryUseCase
import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.RewardCollection
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class RewardListViewModel @Inject constructor(
        private val prefsWrapper: PrefsWrapper,
        private val lotteryUseCase: LotteryUseCase,
        private val getRewardsUseCase: GetRewardsUseCase,
        private val getPointUseCase: GetPointUseCase
) : ViewModel() {

    private lateinit var callback: RewardViewModelCallback
    var rewardList: MutableList<Reward> = mutableListOf()
    var hasSelectReward: MutableLiveData<Boolean> = MutableLiveData()
    private var mutableRewardPoint = MutableLiveData<Int>()
    var rewardPoint: LiveData<Int> = mutableRewardPoint
    var isEmpty: MutableLiveData<Boolean> = MutableLiveData()
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun setCallback(callback: RewardViewModelCallback) {
        this.callback = callback
    }

    fun showRewardDetail() {
        callback.showRewardDetail()
    }

    fun startLottery() {
        viewModelScope.launch {
            val rewards = RewardCollection(rewardList)
            val reward = lotteryUseCase.execute(rewards)
            reward?.let {
                callback.onHitLottery(it)
            } ?: callback.onMissLottery()
        }
    }

    fun loadRewards() {
        viewModelScope.launch {
            getRewardsUseCase.executeAsFlow().collect { newRewardList ->
                isEmpty.value = newRewardList.isNullOrEmpty()
                if (isEmpty.value == true) {
                    return@collect
                }

                rewardList.clear()
                rewardList.addAll(newRewardList)
                callback.showRewards(rewardList)
            }
        }
    }

    fun loadPoint() {
        viewModelScope.launch {
            try {
                callback.onStartLoadingPoint()
                val point = getPointUseCase.execute()
                mutableRewardPoint.value = point.value
            } catch (e: Exception) {
                if (isActive) {
                    callback.onPointLoadFailed()
                }
            } finally {
                callback.onTerminateLoadingPoint()
            }
        }
    }

    fun logout() {
        prefsWrapper.userId = 0
        callback.onLogout()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

interface RewardViewModelCallback {

    fun showRewardDetail()

    fun showRewards(rewardList: MutableList<Reward>)

    fun showError()

    fun onPointLoadFailed()

    fun onStartLoadingPoint()

    fun onTerminateLoadingPoint()

    fun onLogout()

    fun onHitLottery(reward: Reward)

    fun onMissLottery()
}
