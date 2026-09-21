package jp.kztproject.rewardedtodo.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthUseCase
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthUseCase
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val startTodoistAuthUseCase: StartTodoistAuthUseCase,
    private val completeTodoistAuthUseCase: CompleteTodoistAuthUseCase,
) : ViewModel() {

    val uiState: StateFlow<AuthUiState>
        field = MutableStateFlow(AuthUiState())

    // 認可URLはUIを経由してAuth Tabに渡す。画面回転で取りこぼさないようChannelで一度だけ配送する
    private val authorizeRequestChannel = Channel<String>(Channel.BUFFERED)
    val authorizeRequests: Flow<String> = authorizeRequestChannel.receiveAsFlow()

    // 連携が完了したことを一度だけ通知する。ホーム画面への遷移は呼び出し側が行う
    private val authenticatedChannel = Channel<Unit>(Channel.BUFFERED)
    val authenticated: Flow<Unit> = authenticatedChannel.receiveAsFlow()

    /** 認可URLを発行してAuth Tabの起動を要求する。 */
    fun connect() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, error = null) }

            startTodoistAuthUseCase.execute()
                .onSuccess { authorizeUrl -> authorizeRequestChannel.send(authorizeUrl) }
                .onFailure { uiState.update { state -> state.copy(isLoading = false, error = AuthError.UNKNOWN) } }
        }
    }

    /** 端末のブラウザがAuth Tabに対応していないため認可を開始できなかった。 */
    fun onAuthTabUnsupported() {
        uiState.update { it.copy(isLoading = false, error = AuthError.AUTH_TAB_UNSUPPORTED) }
    }

    fun onAuthTabResult(result: TodoistAuthTabResult) {
        when (result) {
            is TodoistAuthTabResult.Succeeded -> completeAuth(result.redirectUri)

            TodoistAuthTabResult.Canceled ->
                uiState.update { it.copy(isLoading = false, error = AuthError.CANCELED) }

            TodoistAuthTabResult.VerificationFailed ->
                uiState.update { it.copy(isLoading = false, error = AuthError.VERIFICATION_FAILED) }
        }
    }

    private fun completeAuth(redirectUri: String) {
        viewModelScope.launch {
            completeTodoistAuthUseCase.execute(redirectUri)
                .onSuccess {
                    uiState.update { AuthUiState() }
                    authenticatedChannel.send(Unit)
                }
                .onFailure { cause ->
                    uiState.update { it.copy(isLoading = false, error = cause.toAuthError()) }
                }
        }
    }

    fun consumeError() {
        uiState.update { it.copy(error = null) }
    }

    private fun Throwable.toAuthError(): AuthError = when (this) {
        is TokenError.StateMismatch -> AuthError.STATE_MISMATCH
        is TokenError.AuthorizationCanceled -> AuthError.CANCELED
        is TokenError.AuthorizationFailed -> AuthError.AUTHORIZATION_FAILED
        is TokenError.ExchangeFailed -> AuthError.EXCHANGE_FAILED
        else -> AuthError.UNKNOWN
    }
}

data class AuthUiState(val isLoading: Boolean = false, val error: AuthError? = null)

enum class AuthError {
    CANCELED,
    VERIFICATION_FAILED,
    STATE_MISMATCH,
    AUTHORIZATION_FAILED,
    EXCHANGE_FAILED,
    AUTH_TAB_UNSUPPORTED,
    UNKNOWN,
}
