package jp.kztproject.rewardedtodo.presentation.auth.todoist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoistAssociationViewModel @Inject constructor(
        private val todoistAuthRepository: ITodoistAccessTokenRepository
) : ViewModel() {

    private val mutableHasAccessToken = MutableLiveData<Boolean>()
    val hasAccessToken: LiveData<Boolean> = mutableHasAccessToken

    fun loadAccessToken() = viewModelScope.launch {
        mutableHasAccessToken.value = todoistAuthRepository.get().isNotEmpty()
    }

    fun clearAccessToken() = viewModelScope.launch {
        todoistAuthRepository.clear()
        loadAccessToken()
    }
}
