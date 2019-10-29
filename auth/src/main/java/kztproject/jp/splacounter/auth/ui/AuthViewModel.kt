package kztproject.jp.splacounter.auth.ui

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kztproject.jp.splacounter.auth.repository.IAuthRepository
import project.seito.auth.R
import javax.inject.Inject

class AuthViewModel @Inject
constructor(private val authRepository: IAuthRepository) : ViewModel() {
    private lateinit var callback: Callback

    var inputString = MutableLiveData<String>()
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    private val mutableDataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = mutableDataLoading

    init {
        inputString.value = ""
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun login() {
        if (inputString.value!!.isEmpty()) {
            callback.onError(R.string.error_login_text_empty)
            return
        }

        if (mutableDataLoading.value == true) {
            return
        }

        mutableDataLoading.value = true

        viewModelScope.launch {
            try {
                authRepository.login(inputString.value!!)
                callback.onSuccessLogin()
            } catch (error: Exception) {
                callback.onFailedLogin(error)
            } finally {
                mutableDataLoading.value = false
            }
        }
    }

    fun signUp() {
        if (inputString.value!!.isEmpty()) {
            callback.onError(R.string.error_login_text_empty)
        } else {

            if (mutableDataLoading.value == true) {
                return
            }

            mutableDataLoading.value = true

            viewModelScope.launch {
                try {
                    authRepository.signUp(inputString.value!!)
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
