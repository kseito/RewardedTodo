package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class RewardListViewModel @Inject constructor(
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
            loadPoint()
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

    fun onHitLottery(reward: Reward)

    fun onMissLottery()
}
