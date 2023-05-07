package jp.kztproject.rewardedtodo.presentation.reward.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val mutableRewardList = MutableLiveData<List<Reward>>()
    val rewardList: LiveData<List<Reward>> = mutableRewardList
    private var mutableRewardPoint = MutableLiveData<Int>()
    var rewardPoint: LiveData<Int> = mutableRewardPoint
    val result = MutableLiveData<Result<Unit>?>()
    private val mutableObtainedReward = MutableStateFlow<Result<Reward?>?>(null)
    val obtainedReward = mutableObtainedReward.asStateFlow()

    init {
        loadRewards()
        loadPoint()
    }

    fun startLottery() {
        viewModelScope.launch {
            val rewards = RewardCollection(mutableRewardList.value!!)
            mutableObtainedReward.value = lotteryUseCase.execute(rewards)
            loadPoint()
        }
    }

    fun resetObtainedReward() {
        mutableObtainedReward.value = null
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
            getPointUseCase.execute().collect {
                mutableRewardPoint.value = it.value
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
