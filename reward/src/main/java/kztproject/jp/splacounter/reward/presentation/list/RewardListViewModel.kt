package kztproject.jp.splacounter.reward.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import kztproject.jp.splacounter.reward.application.usecase.DeleteRewardUseCase
import kztproject.jp.splacounter.reward.application.usecase.GetRewardsUseCase
import kztproject.jp.splacounter.reward.application.usecase.LotteryUseCase
import kztproject.jp.splacounter.reward.domain.model.RewardCollection
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import project.seito.screen_transition.preference.PrefsWrapper
import javax.inject.Inject

class RewardListViewModel @Inject constructor(
        private val rewardListClient: IPointRepository,
        private val rewardDao: IRewardRepository,
        private val prefsWrapper: PrefsWrapper,
        private val lotteryUseCase: LotteryUseCase,
        private val getRewardsUseCase: GetRewardsUseCase,
        private val deleteRewardUseCase: DeleteRewardUseCase
) : ViewModel() {

    private lateinit var callback: RewardViewModelCallback
    var rewardEntityList: MutableList<RewardEntity> = mutableListOf()
    var selectedRewardEntity: RewardEntity? = null
        set(value) {
            hasSelectReward.value = value != null
            field = value
        }
    var hasSelectReward: MutableLiveData<Boolean> = MutableLiveData()
    private var mutableRewardPoint = MutableLiveData<Int>()
    var rewardPoint: LiveData<Int> = mutableRewardPoint
    var isEmpty: MutableLiveData<Boolean> = MutableLiveData()
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun setCallback(callback: RewardViewModelCallback) {
        this.callback = callback
    }

    fun setPoint(point: Int) {
        mutableRewardPoint.value = point
    }

    fun showRewardDetail() {
        callback.showRewardDetail()
    }

    fun startLottery() {
        viewModelScope.launch {
            val rewards = RewardCollection.convertFrom(rewardEntityList)
            val reward = lotteryUseCase.execute(rewards)
            reward?.let {
                callback.onHitLottery(RewardEntity.from(it))
            } ?: callback.onMissLottery()
        }
    }

    fun loadRewards() {
        viewModelScope.launch {
            val newRewardList = rewardDao.findAll()
            isEmpty.value = newRewardList.isNullOrEmpty()
            if (isEmpty.value == true) {
                return@launch
            }

            rewardEntityList.clear()
            rewardEntityList.addAll(newRewardList)
            callback.showRewards(rewardEntityList)
        }
    }

    fun loadPoint() {
        viewModelScope.launch {
            try {
                callback.onStartLoadingPoint()
                val point = rewardListClient.loadPoint(prefsWrapper.userId)
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

    fun acquireReward() {
        val selectedRewardEntity: RewardEntity = this.selectedRewardEntity
                ?: throw NullPointerException("acquireReward() cannot call when selectedReward is null")
        if (rewardPoint.value!! >= selectedRewardEntity.consumePoint) {
            viewModelScope.launch {
                try {
                    val user = rewardListClient.consumePoint(prefsWrapper.userId, selectedRewardEntity.consumePoint)
                    callback.successAcquireReward(selectedRewardEntity, user.point)
                    mutableRewardPoint.value = user.point
                } catch (e: Exception) {
                    if (isActive) {
                        callback.showError()
                    }
                }
            }
        } else {
            callback.showError()
        }
    }

    fun confirmDelete() {
        callback.showDeleteConfirmDialog(selectedRewardEntity!!)
    }

    fun deleteRewardIfNeeded(rewardEntity: RewardEntity) {
        if (rewardEntity.needRepeat) {
            return
        }

        deleteReward(rewardEntity, false)
    }

    fun editReward() {
        callback.onRewardEditSelected(selectedRewardEntity!!)
        selectedRewardEntity = null
    }

    fun deleteReward(rewardEntity: RewardEntity, needCallback: Boolean) {
        viewModelScope.launch {
            rewardDao.delete(rewardEntity)

            if (needCallback) {
                selectedRewardEntity = null
                callback.onRewardDeleted(rewardEntity)
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

    fun showRewards(rewardEntityList: MutableList<RewardEntity>)

    fun showDeleteConfirmDialog(rewardEntity: RewardEntity)

    fun showError()

    fun successAcquireReward(rewardEntity: RewardEntity, point: Int)

    fun onRewardSelected(position: Int)

    fun onRewardDeSelected(position: Int)

    fun onRewardDeleted(rewardEntity: RewardEntity)

    fun onRewardEditSelected(rewardEntity: RewardEntity)

    fun onPointLoadFailed()

    fun onStartLoadingPoint()

    fun onTerminateLoadingPoint()

    fun onLogout()

    fun onHitLottery(rewardEntity: RewardEntity)

    fun onMissLottery()
}
