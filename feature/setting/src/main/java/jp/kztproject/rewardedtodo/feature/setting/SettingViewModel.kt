package jp.kztproject.rewardedtodo.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val disconnectTodoistUseCase: DisconnectTodoistUseCase) :
    ViewModel() {

    val uiState: StateFlow<SettingUiState>
        field = MutableStateFlow(SettingUiState())

    // ログアウトの完了を一度だけ通知する。認証画面への遷移は呼び出し側が行う
    private val loggedOutChannel = Channel<Unit>(Channel.BUFFERED)
    val loggedOut: Flow<Unit> = loggedOutChannel.receiveAsFlow()

    fun requestLogout() {
        uiState.update { it.copy(isConfirmingLogout = true) }
    }

    fun dismissLogoutConfirmation() {
        uiState.update { it.copy(isConfirmingLogout = false) }
    }

    fun logout() {
        viewModelScope.launch {
            uiState.update { it.copy(isConfirmingLogout = false, isLoading = true, error = null) }

            disconnectTodoistUseCase.execute()
                .onSuccess { loggedOutChannel.send(Unit) }
                // 失敗してもローディングは必ず解除し、画面が固まらないようにする
                .onFailure { uiState.update { it.copy(isLoading = false, error = SettingError.LOGOUT_FAILED) } }
        }
    }

    fun consumeError() {
        uiState.update { it.copy(error = null) }
    }
}

data class SettingUiState(
    val isConfirmingLogout: Boolean = false,
    val isLoading: Boolean = false,
    val error: SettingError? = null,
)

enum class SettingError {
    LOGOUT_FAILED,
}
