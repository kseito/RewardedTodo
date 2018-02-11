package kztproject.jp.splacounter.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.StringRes
import kztproject.jp.splacounter.BR
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.model.Reward

class RewardAddViewModel : BaseObservable() {

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

        //TODO save new reward to database

        callback.onSaveCompleted()
    }
}

interface RewardAddViewModelCallback {

    fun onSaveCompleted()

    fun onError(@StringRes resourceId: Int)
}