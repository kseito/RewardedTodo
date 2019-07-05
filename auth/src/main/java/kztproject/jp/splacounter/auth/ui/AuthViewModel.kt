package kztproject.jp.splacounter.auth.ui

import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import androidx.annotation.StringRes
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kztproject.jp.splacounter.auth.repository.IAuthRepository
import project.seito.auth.R
import javax.inject.Inject

class AuthViewModel @Inject
constructor(private val authRepository: IAuthRepository) : ViewModel() {
    private lateinit var callback: Callback

    var inputString = ObservableField<String>()
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    init {
        inputString.set("")
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun login() {
        if (inputString.get()!!.isEmpty()) {
            callback.onError(R.string.error_login_text_empty)
            return
        }

        viewModelScope.launch {
            try {
                callback.onStartAsyncProcess()
                authRepository.login(inputString.get()!!)
                callback.onSuccessLogin()
            } catch (error: Exception) {
                callback.onFailedLogin(error)
            } finally {
                callback.onFinishAsyncProcess()
            }
        }
    }

    fun signUp() {
        if (inputString.get()!!.isEmpty()) {
            callback.onError(R.string.error_login_text_empty)
        } else {
            viewModelScope.launch {
                try {
                    callback.onStartAsyncProcess()
                    authRepository.signUp(inputString.get()!!)
                    callback.onSuccessSignUp()
                } catch (error: Exception) {
                    callback.onFailedSignUp()
                } finally {
                    callback.onFinishAsyncProcess()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    interface Callback {
        fun onStartAsyncProcess()

        fun onFinishAsyncProcess()

        fun onSuccessSignUp()

        fun onFailedSignUp()

        fun onSuccessLogin()

        fun onFailedLogin(e: Throwable)

        fun onError(@StringRes stringId: Int)
    }
}
