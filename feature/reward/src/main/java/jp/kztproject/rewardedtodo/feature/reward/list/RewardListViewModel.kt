package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.model.error.OverMaxRewardsException
import jp.kztproject.rewardedtodo.application.reward.usecase.BatchLotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.BatchLotteryResult
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardListViewModel @Inject constructor(
    private val lotteryUseCase: LotteryUseCase,
    private val batchLotteryUseCase: BatchLotteryUseCase,
    private val getRewardsUseCase: GetRewardsUseCase,
    private val getPointUseCase: GetPointUseCase,
    private val saveRewardUseCase: SaveRewardUseCase,
    private val deleteRewardUseCase: DeleteRewardUseCase,
) : ViewModel() {

    val rewardList: StateFlow<List<Reward>> = flow {
        getRewardsUseCase.executeAsFlow().collect { emit(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )
    private val mutableRewardPoint = MutableStateFlow(0)
    val rewardPoint: StateFlow<Int> = mutableRewardPoint.asStateFlow()
    private val mutableResult = MutableStateFlow<Result<Unit>?>(null)
    val result: StateFlow<Result<Unit>?> = mutableResult.asStateFlow()
    private val mutableObtainedReward = MutableStateFlow<Result<Reward?>?>(null)
    val obtainedReward = mutableObtainedReward.asStateFlow()
    private val mutableBatchLotteryResult = MutableStateFlow<Result<BatchLotteryResult>?>(null)
    val batchLotteryResult = mutableBatchLotteryResult.asStateFlow()

    init {
        loadPoint()
    }

    fun startLottery() {
        viewModelScope.launch {
            val rewards = RewardCollection(rewardList.value)
            mutableObtainedReward.value = lotteryUseCase.execute(rewards)
            loadPoint()
        }
    }

    fun resetObtainedReward() {
        mutableObtainedReward.value = null
    }

    fun startBatchLottery(count: Int = 10) {
        viewModelScope.launch {
            val rewards = RewardCollection(rewardList.value)
            mutableBatchLotteryResult.value = batchLotteryUseCase.execute(rewards, count)
            loadPoint()
        }
    }

    fun resetBatchLotteryResult() {
        mutableBatchLotteryResult.value = null
    }

    fun validateRewards(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val rewards = rewardList.value
            if (rewards.size >= RewardCollection.MAX) {
                // TODO Create error property to show this error
                mutableObtainedReward.value = Result.failure(OverMaxRewardsException())
            } else {
                onSuccess()
            }
        }
    }

    fun loadPoint() {
        viewModelScope.launch {
            getPointUseCase.execute().collect {
                mutableRewardPoint.value = it.value
            }
        }
    }

    fun saveReward(reward: RewardInput) {
        viewModelScope.launch {
            val newResult = saveRewardUseCase.execute(reward)
            mutableResult.value = newResult
        }
    }

    fun clearResult() {
        mutableResult.value = null
    }

    fun deleteReward(reward: Reward) {
        // TODO show confirmation dialog
        viewModelScope.launch {
            deleteRewardUseCase.execute(reward)
            mutableResult.value = Result.success(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
