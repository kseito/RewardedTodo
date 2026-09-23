package jp.kztproject.rewardedtodo.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ログアウトの完了は[uiState]の更新として表す。
 * 理由は[jp.kztproject.rewardedtodo.feature.auth.AuthViewModel]と同じ。
 */
@HiltViewModel
class SettingViewModel @Inject constructor(private val disconnectTodoistUseCase: DisconnectTodoistUseCase) :
    ViewModel() {

    val uiState: StateFlow<SettingUiState>
        field = MutableStateFlow(SettingUiState())

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
                .onSuccess { uiState.update { SettingUiState(isLoggedOut = true) } }
                // 失敗してもローディングは必ず解除し、画面が固まらないようにする
                .onFailure { uiState.update { it.copy(isLoading = false, error = SettingError.LOGOUT_FAILED) } }
        }
    }

    /** ログアウト完了を画面が処理し終えた。 */
    fun consumeLoggedOut() {
        uiState.update { it.copy(isLoggedOut = false) }
    }

    fun consumeError() {
        uiState.update { it.copy(error = null) }
    }
}

data class SettingUiState(
    val isConfirmingLogout: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: SettingError? = null,
)

enum class SettingError {
    LOGOUT_FAILED,
}
