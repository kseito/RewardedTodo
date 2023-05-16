package jp.kztproject.rewardedtodo.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoistAuthViewModel @Inject constructor(
        private val todoistAccessTokenRepository: ITodoistAccessTokenRepository
) : ViewModel() {

    interface Callback {
        fun onRequireAccessTokenSuccess()

        fun onRequireAccessTokenFailed()
    }

    lateinit var callback: Callback

    fun requireAccessToken(clientId: String, clientSecret: String, code: String) {
        viewModelScope.launch {
            try {
                todoistAccessTokenRepository.refresh(clientId, clientSecret, code)
                callback.onRequireAccessTokenSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onRequireAccessTokenFailed()
            }
        }
    }
}
