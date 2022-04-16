package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.*
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardListViewModel @Inject constructor(
    private val lotteryUseCase: LotteryUseCase,
    private val getRewardsUseCase: GetRewardsUseCase,
    private val getPointUseCase: GetPointUseCase,
    private val saveRewardUseCase: SaveRewardUseCase,
    private val deleteRewardUseCase: DeleteRewardUseCase
) : ViewModel() {

    private lateinit var callback: RewardViewModelCallback
    private val mutableRewardList = MutableLiveData<List<Reward>>()
    val rewardList: LiveData<List<Reward>> = mutableRewardList
    private var mutableRewardPoint = MutableLiveData<Int>()
    var rewardPoint: LiveData<Int> = mutableRewardPoint
    val result = MutableLiveData<Result<Unit>?>()

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

    fun saveReward(reward: RewardInput) {
        viewModelScope.launch {
            val newResult = saveRewardUseCase.execute(reward)
            result.value = newResult
        }
    }

    fun deleteReward(reward: Reward) {
        // TODO show confirmation dialog
        viewModelScope.launch {
            deleteRewardUseCase.execute(reward)
            result.value = Result.success(Unit)
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
