package kztproject.jp.splacounter.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.StringRes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.BR
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.database.RewardDao
import kztproject.jp.splacounter.model.Reward
import javax.inject.Inject

class RewardAddViewModel @Inject constructor(val rewardDao: RewardDao) : BaseObservable() {

    @Bindable
    private var name: String = ""

    @Bindable
    private var description: String = ""

    @Bindable
    private var point: String = "0"

    private lateinit var callback: RewardAddViewModelCallback

    fun setCallback(callback: RewardAddViewModelCallback) {
        this.callback = callback
    }

    fun setName(name: String) {
        this.name = name
        notifyPropertyChanged(BR.name)
    }

    fun setDescription(description: String) {
        this.description = description
        notifyPropertyChanged(BR.description)
    }

    fun setPoint(point: String) {
        this.point = point
        notifyPropertyChanged(BR.point)
    }

    fun saveReward() {

        if (name.isEmpty()) {
            callback.onError(R.string.error_empty_title)
            return
        } else if (point.isEmpty() || point == "0") {
            callback.onError(R.string.error_empty_point)
            return
        }

        val reward = Reward(0, name, point.toInt(), description)

        Single.create<Reward> {
            rewardDao.insertReward(reward)
            it.onSuccess(reward)
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(Consumer { callback.onSaveCompleted(it.name) })
    }
}

interface RewardAddViewModelCallback {

    fun onSaveCompleted(rewardName: String)

    fun onError(@StringRes resourceId: Int)
}