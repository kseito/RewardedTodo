package kztproject.jp.splacounter.reward.detail.ui

import androidx.lifecycle.ViewModel
import android.content.res.Resources
import androidx.databinding.ObservableField
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import project.seito.reward.R
import javax.inject.Inject

class RewardDetailViewModel @Inject constructor(private val rewardRepository: IRewardRepository) : ViewModel() {

    var reward: ObservableField<Reward> = ObservableField()
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    init {
        reward.set(Reward())
    }

    private lateinit var callback: RewardDetailViewModelCallback

    fun initialize(id: Int) {
        viewModelScope.launch {
            val reward = rewardRepository.findBy(id) ?: throw Resources.NotFoundException()
            this@RewardDetailViewModel.reward.set(reward)
        }
    }

    fun setCallback(callback: RewardDetailViewModelCallback) {
        this.callback = callback
    }

    fun saveReward() {
        val reward = this.reward.get() ?: throw IllegalStateException("reward is null")
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