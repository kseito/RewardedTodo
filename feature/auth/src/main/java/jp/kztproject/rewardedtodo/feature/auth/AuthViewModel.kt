package jp.kztproject.rewardedtodo.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthUseCase
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthUseCase
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 認可URLの発行と連携完了は、いずれも[uiState]の更新として表す。
 *
 * ViewModelのイベントを Channel で流すと、ViewModel が画面より長生きしたときに配送が保証されない。
 * 画面が処理し終えたら consume 系のメソッドで状態を戻す。
 * https://developer.android.com/topic/architecture/ui-layer/events
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val startTodoistAuthUseCase: StartTodoistAuthUseCase,
    private val completeTodoistAuthUseCase: CompleteTodoistAuthUseCase,
) : ViewModel() {

    val uiState: StateFlow<AuthUiState>
        field = MutableStateFlow(AuthUiState())

    /** 認可URLを発行してAuth Tabの起動を要求する。 */
    fun connect() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, error = null) }

            startTodoistAuthUseCase.execute()
                .onSuccess { authorizeUrl -> uiState.update { it.copy(authorizeUrl = authorizeUrl) } }
                .onFailure { uiState.update { it.copy(isLoading = false, error = AuthError.UNKNOWN) } }
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
                .onSuccess { uiState.update { AuthUiState(isAuthenticated = true) } }
                .onFailure { cause ->
                    uiState.update { it.copy(isLoading = false, error = cause.toAuthError()) }
                }
        }
    }

    /** 認可URLをAuth Tabへ渡し終えた。消さないと再購読のたびにAuth Tabが開く。 */
    fun consumeAuthorizeUrl() {
        uiState.update { it.copy(authorizeUrl = null) }
    }

    /** 連携完了を画面が処理し終えた。 */
    fun consumeAuthenticated() {
        uiState.update { it.copy(isAuthenticated = false) }
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

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val authorizeUrl: String? = null,
    val isAuthenticated: Boolean = false,
)

enum class AuthError {
    CANCELED,
    VERIFICATION_FAILED,
    STATE_MISMATCH,
    AUTHORIZATION_FAILED,
    EXCHANGE_FAILED,
    AUTH_TAB_UNSUPPORTED,
    UNKNOWN,
}
