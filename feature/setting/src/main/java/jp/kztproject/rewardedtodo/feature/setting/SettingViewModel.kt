package jp.kztproject.rewardedtodo.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.data.auth.ITodoistAccessTokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
        private val todoistAuthRepository: ITodoistAccessTokenRepository
) : ViewModel() {

    private val mutableHasAccessToken = MutableStateFlow(false)
    val hasAccessToken: StateFlow<Boolean> = mutableHasAccessToken

    init {
        loadAccessToken()
    }

    fun loadAccessToken() = viewModelScope.launch {
        mutableHasAccessToken.value = todoistAuthRepository.get().isNotEmpty()
    }

    fun clearAccessToken() = viewModelScope.launch {
        todoistAuthRepository.clear()
        loadAccessToken()
    }
}
