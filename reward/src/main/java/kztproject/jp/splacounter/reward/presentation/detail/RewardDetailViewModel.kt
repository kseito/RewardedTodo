package kztproject.jp.splacounter.reward.presentation.detail

import androidx.lifecycle.ViewModel
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity
import kztproject.jp.splacounter.reward.application.repository.IRewardRepository
import project.seito.reward.R
import javax.inject.Inject

class RewardDetailViewModel @Inject constructor(private val rewardRepository: IRewardRepository) : ViewModel() {

    private var mutableReward =  MutableLiveData<RewardEntity>()
    var rewardEntity: LiveData<RewardEntity> = mutableReward
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    init {
        mutableReward.value = RewardEntity()
    }

    private lateinit var callback: RewardDetailViewModelCallback

    fun initialize(id: Int) {
        viewModelScope.launch {
            val reward = rewardRepository.findBy(id) ?: throw Resources.NotFoundException()
            mutableReward.value = reward
        }
    }

    fun setCallback(callback: RewardDetailViewModelCallback) {
        this.callback = callback
    }

    fun saveReward() {
        val reward = this.rewardEntity.value ?: throw IllegalStateException("mutableReward is null")
        if (reward.name.isEmpty()) {
            callback.onError(R.string.error_empty_title)
            return
        } else if (reward.consumePoint == 0) {
            callback.onError(R.string.error_empty_point)
            return
        }

        viewModelScope.launch {
            rewardRepository.createOrUpdate(reward)
            callback.onSaveCompleted(reward.name)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

interface RewardDetailViewModelCallback {

    fun onSaveCompleted(rewardName: String)

    fun onError(@StringRes resourceId: Int)
}