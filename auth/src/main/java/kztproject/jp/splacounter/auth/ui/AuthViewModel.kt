package kztproject.jp.splacounter.auth.ui

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.support.annotation.StringRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kztproject.jp.splacounter.auth.repository.IAuthRepository
import project.seito.auth.R
import project.seito.screen_transition.extention.addTo
import javax.inject.Inject

class AuthViewModel @Inject
constructor(private val authRepository: IAuthRepository) : ViewModel() {
    private lateinit var callback: Callback

    var inputString = ObservableField<String>()
    private val compositeDisposable = CompositeDisposable()

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
                    .addTo(compositeDisposable)
        }
    }

    fun signUp() {
        if (inputString.get()!!.isEmpty()) {
            callback.showError(R.string.error_login_text_empty)
        } else {
            authRepository.signUp(inputString.get()!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { callback.showProgressDialog() }
                    .doOnTerminate { callback.dismissProgressDialog() }
                    .subscribe({ callback.signUpSucceeded() }, { callback.showError(R.string.error_sign_up) })
                    .addTo(compositeDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    interface Callback {
        fun showProgressDialog()

        fun dismissProgressDialog()

        fun signUpSucceeded()

        fun loginSucceeded()

        fun loginFailed(e: Throwable)

        fun showError(@StringRes stringId: Int)
    }
}
