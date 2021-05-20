package jp.kztproject.rewardedtodo.presentation.auth.todoist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class TodoistAuthViewModel @Inject constructor(
        private val todoistAccessTokenRepository: ITodoistAccessTokenRepository
) : ViewModel() {

    interface Callback {
        fun onRequireAccessTokenSuccess(accessToken: String)

        fun onRequireAccessTokenFailed()
    }
    lateinit var callback: Callback

    fun requireAccessToken(clientId: String, clientSecret: String, code: String) {
        viewModelScope.launch {
            try {
                val accessToken = todoistAccessTokenRepository.fetch(clientId, clientSecret, code)
                callback.onRequireAccessTokenSuccess(accessToken)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onRequireAccessTokenFailed()
            }
        }
    }
}