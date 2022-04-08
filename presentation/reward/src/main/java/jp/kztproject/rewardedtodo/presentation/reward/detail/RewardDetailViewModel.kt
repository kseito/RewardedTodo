package jp.kztproject.rewardedtodo.presentation.reward.detail

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.presentation.reward.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardDetailViewModel @Inject constructor(
        private val deleteRewardUseCase: DeleteRewardUseCase,
        private val getRewardUseCase: GetRewardUseCase,
        private val saveRewardUseCase: SaveRewardUseCase
) : ViewModel() {

    private var reward: Reward? = null
    private var mutableRewardInput = MutableLiveData<RewardInput>()
    var rewardInput: LiveData<RewardInput> = mutableRewardInput
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)
    val canDeleteReward: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        mutableRewardInput.value = RewardInput()
    }

    private lateinit var callback: RewardDetailViewModelCallback

    fun initialize(id: RewardId) {
        viewModelScope.launch {
            val reward = getRewardUseCase.execute(id) ?: throw Resources.NotFoundException()
            mutableRewardInput.value = RewardInput.from(reward)
            this@RewardDetailViewModel.reward = reward
            canDeleteReward.value = true
        }
    }

    fun setCallback(callback: RewardDetailViewModelCallback) {
        this.callback = callback
    }

    fun saveReward() {
        val reward = this.rewardInput.value ?: throw IllegalStateException("mutableReward is null")
        if (reward.name.isNullOrEmpty()) {
            callback.onError(R.string.error_empty_title)
            return
        }

        viewModelScope.launch {
            kotlin.runCatching {
                saveRewardUseCase.execute(reward)
            }.onSuccess {
                callback.onSaveCompleted(reward.name!!)
            }.onFailure {
                val errorMessageId = ErrorMessageClassifier(it).messageId
                callback.onError(errorMessageId)
            }
        }
    }

    fun confirmToRewardDeletion() {
        reward?.let {
            callback.onConfirmToRewardDeletion(it)
        }
    }

    fun deleteReward() {
        viewModelScope.launch {
            reward?.let {
                deleteRewardUseCase.execute(it)
                callback.onDeleteCompleted(it.name)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

interface RewardDetailViewModelCallback {

    fun onSaveCompleted(rewardName: String)

    fun onConfirmToRewardDeletion(reward: Reward)

    fun onDeleteCompleted(rewardName: RewardName)

    fun onError(@StringRes resourceId: Int)
}
