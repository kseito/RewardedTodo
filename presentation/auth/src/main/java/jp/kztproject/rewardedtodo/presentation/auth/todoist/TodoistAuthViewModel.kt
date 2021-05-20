package jp.kztproject.rewardedtodo.presentation.auth.todoist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoistAuthViewModel @Inject constructor(
        private val todoistAccessTokenRepository: ITodoistAccessTokenRepository
) : ViewModel() {

    fun requireAccessToken() {
        viewModelScope.launch {
            //TODO use TodoistAccessTokenRepository
        }
    }
}