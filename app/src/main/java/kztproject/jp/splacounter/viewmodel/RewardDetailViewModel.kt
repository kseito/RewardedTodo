package kztproject.jp.splacounter.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.StringRes
import com.android.databinding.library.baseAdapters.BR
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.database.RewardDao
import kztproject.jp.splacounter.database.model.Reward
import javax.inject.Inject

class RewardDetailViewModel @Inject constructor(private val rewardDao: RewardDao) : BaseObservable() {

    @Bindable
    var reward: Reward = Reward()

    private lateinit var callback: RewardDetailViewModelCallback

    fun initialize(id: Int) {
        Single.create<Reward> { emitter ->
            val reward = rewardDao.findBy(id)
            emitter.onSuccess(reward)
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    reward -> this.reward = reward
                    notifyPropertyChanged(BR.reward)
                })
    }

    fun setCallback(callback: RewardDetailViewModelCallback) {
        this.callback = callback
    }

    fun saveReward() {

        if (reward.name.isEmpty()) {
            callback.onError(R.string.error_empty_title)
            return
        } else if (reward.consumePoint == 0) {
            callback.onError(R.string.error_empty_point)
            return
        }

        Single.create<Reward> {
            rewardDao.insertReward(reward)
            it.onSuccess(reward)
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer { callback.onSaveCompleted(it.name) })
    }
}

interface RewardDetailViewModelCallback {

    fun onSaveCompleted(rewardName: String)

    fun onError(@StringRes resourceId: Int)
}