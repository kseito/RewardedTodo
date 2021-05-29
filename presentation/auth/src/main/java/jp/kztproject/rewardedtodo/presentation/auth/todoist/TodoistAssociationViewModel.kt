package jp.kztproject.rewardedtodo.presentation.auth.todoist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoistAssociationViewModel @Inject constructor(
        private val todoistAuthRepository: ITodoistAccessTokenRepository
): ViewModel() {

    private val mutableHasAccessToken = MutableLiveData<Boolean>()
    val hasAccessToken : LiveData<Boolean> = mutableHasAccessToken

    fun loadAccessToken() = viewModelScope.launch {
        mutableHasAccessToken.value = todoistAuthRepository.get().isNotEmpty()
    }
}