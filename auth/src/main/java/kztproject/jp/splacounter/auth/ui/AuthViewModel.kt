package kztproject.jp.splacounter.auth.ui

import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val mutableDataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = mutableDataLoading

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

        if (mutableDataLoading.value == true) {
            return
        }

        mutableDataLoading.value = true

        viewModelScope.launch {
            try {
                authRepository.login(inputString.get()!!)
                callback.onSuccessLogin()
            } catch (error: Exception) {
                callback.onFailedLogin(error)
            } finally {
                mutableDataLoading.value = false
            }
        }
    }

    fun signUp() {
        if (inputString.get()!!.isEmpty()) {
            callback.onError(R.string.error_login_text_empty)
        } else {

            if (mutableDataLoading.value == true) {
                return
            }

            mutableDataLoading.value = true

            viewModelScope.launch {
                try {
                    authRepository.signUp(inputString.get()!!)
                    callback.onSuccessSignUp()
                } catch (error: Exception) {
                    callback.onFailedSignUp()
                } finally {
                    mutableDataLoading.value = false
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    interface Callback {
        fun onSuccessSignUp()

        fun onFailedSignUp()

        fun onSuccessLogin()

        fun onFailedLogin(e: Throwable)

        fun onError(@StringRes stringId: Int)
    }
}
