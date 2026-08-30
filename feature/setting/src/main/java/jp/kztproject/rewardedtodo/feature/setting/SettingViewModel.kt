package jp.kztproject.rewardedtodo.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthUseCase
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistUseCase
import jp.kztproject.rewardedtodo.application.todo.GetTodoistCredentialUseCase
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthUseCase
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getTodoistCredentialUseCase: GetTodoistCredentialUseCase,
    private val startTodoistAuthUseCase: StartTodoistAuthUseCase,
    private val completeTodoistAuthUseCase: CompleteTodoistAuthUseCase,
    private val disconnectTodoistUseCase: DisconnectTodoistUseCase,
) : ViewModel() {

    // ローディングとエラーなど、ユーザー操作で変わる状態のみ保持する。
    // 接続状態はクレデンシャルのFlowから導出するため持たない。
    private val editState = MutableStateFlow(ConnectionEditState())

    // 認可URLはUIを経由してAuth Tabに渡す。画面回転で取りこぼさないようChannelで一度だけ配送する
    private val authorizeRequestChannel = Channel<String>(Channel.BUFFERED)
    val authorizeRequests: Flow<String> = authorizeRequestChannel.receiveAsFlow()

    val hasAccessToken: StateFlow<Boolean> = getTodoistCredentialUseCase.executeAsFlow()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MILLIS),
            initialValue = false,
        )

    val uiState: StateFlow<TodoistConnectionUiState> = combine(
        getTodoistCredentialUseCase.executeAsFlow(),
        editState,
    ) { credential, edit ->
        TodoistConnectionUiState(
            isConnected = credential != null,
            isLoading = edit.isLoading,
            error = edit.error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MILLIS),
        initialValue = TodoistConnectionUiState(),
    )

    /** 認可URLを発行してAuth Tabの起動を要求する。 */
    fun connect() {
        viewModelScope.launch {
            editState.update { it.copy(isLoading = true, error = null) }

            startTodoistAuthUseCase.execute()
                .onSuccess { authorizeUrl -> authorizeRequestChannel.send(authorizeUrl) }
                .onFailure { editState.update { it.copy(isLoading = false, error = TodoistAuthError.UNKNOWN) } }
        }
    }

    /** 端末のブラウザがAuth Tabに対応していないため認可を開始できなかった。 */
    fun onAuthTabUnsupported() {
        editState.update { it.copy(isLoading = false, error = TodoistAuthError.AUTH_TAB_UNSUPPORTED) }
    }

    fun onAuthTabResult(result: TodoistAuthTabResult) {
        when (result) {
            is TodoistAuthTabResult.Succeeded -> completeAuth(result.redirectUri)

            TodoistAuthTabResult.Canceled ->
                editState.update { it.copy(isLoading = false, error = TodoistAuthError.CANCELED) }

            TodoistAuthTabResult.VerificationFailed ->
                editState.update { it.copy(isLoading = false, error = TodoistAuthError.VERIFICATION_FAILED) }
        }
    }

    private fun completeAuth(redirectUri: String) {
        viewModelScope.launch {
            completeTodoistAuthUseCase.execute(redirectUri)
                // 成功するとクレデンシャルFlowが再emitされ、isConnectedは自動更新される
                .onSuccess { editState.value = ConnectionEditState() }
                .onFailure { cause ->
                    editState.update { it.copy(isLoading = false, error = cause.toAuthError()) }
                }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            editState.update { it.copy(isLoading = true, error = null) }

            disconnectTodoistUseCase.execute()
                // 削除でクレデンシャルFlowが再emitされ、isConnectedは自動更新される
                .onSuccess { editState.value = ConnectionEditState() }
                // 解除に失敗してもローディングは必ず解除し、画面が固まらないようにする
                .onFailure { editState.update { it.copy(isLoading = false, error = TodoistAuthError.UNKNOWN) } }
        }
    }

    fun consumeError() {
        editState.update { it.copy(error = null) }
    }

    private fun Throwable.toAuthError(): TodoistAuthError = when (this) {
        is TokenError.StateMismatch -> TodoistAuthError.STATE_MISMATCH
        is TokenError.AuthorizationCanceled -> TodoistAuthError.CANCELED
        is TokenError.AuthorizationFailed -> TodoistAuthError.AUTHORIZATION_FAILED
        is TokenError.ExchangeFailed -> TodoistAuthError.EXCHANGE_FAILED
        else -> TodoistAuthError.UNKNOWN
    }

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
    }
}

private data class ConnectionEditState(val isLoading: Boolean = false, val error: TodoistAuthError? = null)

data class TodoistConnectionUiState(
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val error: TodoistAuthError? = null,
)

enum class TodoistAuthError {
    CANCELED,
    VERIFICATION_FAILED,
    STATE_MISMATCH,
    AUTHORIZATION_FAILED,
    EXCHANGE_FAILED,
    AUTH_TAB_UNSUPPORTED,
    UNKNOWN,
}
