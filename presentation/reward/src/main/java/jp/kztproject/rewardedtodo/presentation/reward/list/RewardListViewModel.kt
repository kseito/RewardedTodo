package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class RewardListViewModel @Inject constructor(
        private val lotteryUseCase: LotteryUseCase,
        private val getRewardsUseCase: GetRewardsUseCase,
        private val getPointUseCase: GetPointUseCase
) : ViewModel() {

    private lateinit var callback: RewardViewModelCallback
    private val mutableRewardList = MutableLiveData<List<Reward>>()
    val rewardList: LiveData<List<Reward>> = mutableRewardList
    private var mutableRewardPoint = MutableLiveData<Int>()
    var rewardPoint: LiveData<Int> = mutableRewardPoint

    fun setCallback(callback: RewardViewModelCallback) {
        this.callback = callback
    }

    fun startLottery() {
        viewModelScope.launch {
            val rewards = RewardCollection(mutableRewardList.value!!)
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
                mutableRewardList.value = newRewardList
            }
        }
    }

    fun loadPoint() {
        viewModelScope.launch {
            try {
                val point = getPointUseCase.execute()
                mutableRewardPoint.value = point.value
            } catch (e: Exception) {
                if (isActive) {
                    callback.onPointLoadFailed()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

interface RewardViewModelCallback {

    fun onPointLoadFailed()

    fun onHitLottery(reward: Reward)

    fun onMissLottery()
}
