package kztproject.jp.splacounter.reward.detail.ui

import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import android.databinding.ObservableField
import android.support.annotation.StringRes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kztproject.jp.splacounter.reward.database.model.Reward
import kztproject.jp.splacounter.reward.repository.IRewardRepository
import project.seito.reward.R
import project.seito.screen_transition.extention.addTo
import javax.inject.Inject

class RewardDetailViewModel @Inject constructor(private val rewardRepository: IRewardRepository) : ViewModel() {

    var reward: ObservableField<Reward> = ObservableField()
    private val compositeDisposable = CompositeDisposable()

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    init {
        reward.set(Reward())
    }

    private lateinit var callback: RewardDetailViewModelCallback

    fun initialize(id: Int) {
        Single.create<Reward> { emitter ->
            val reward = rewardRepository.findBy(id) ?: throw Resources.NotFoundException()
            emitter.onSuccess(reward)
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { reward ->
                    this.reward.set(reward)
                }
                .addTo(compositeDisposable)
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
        compositeDisposable.dispose()
        viewModelScope.cancel()
    }
}

interface RewardDetailViewModelCallback {

    fun onSaveCompleted(rewardName: String)

    fun onError(@StringRes resourceId: Int)
}