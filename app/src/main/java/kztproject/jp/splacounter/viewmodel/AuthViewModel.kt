package kztproject.jp.splacounter.viewmodel

import android.databinding.ObservableField
import android.support.annotation.StringRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.R
import kztproject.jp.splacounter.repository.AuthRepository
import javax.inject.Inject

class AuthViewModel @Inject
constructor(private val authRepository: AuthRepository) {
    private lateinit var callback: Callback

    var inputString = ObservableField<String>()

    init {
        inputString.set("")
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun login() {
        if (inputString.get()!!.isEmpty()) {
            callback.showError(R.string.error_login_text_empty)
        } else {
            authRepository.login(inputString.get()!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { callback.showProgressDialog() }
                    .doOnTerminate { callback.dismissProgressDialog() }
                    .subscribe(
                            { callback.loginSucceeded() }
                    ) { e -> callback.loginFailed(e) }
        }
    }

    interface Callback {
        fun showProgressDialog()

        fun dismissProgressDialog()

        fun loginSucceeded()

        fun loginFailed(e: Throwable)

        fun showError(@StringRes stringId: Int)
    }
}
