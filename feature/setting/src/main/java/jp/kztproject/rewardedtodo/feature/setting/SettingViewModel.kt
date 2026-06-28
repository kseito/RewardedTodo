package jp.kztproject.rewardedtodo.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.DeleteApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.SaveApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getApiTokenUseCase: GetApiTokenUseCase,
    private val saveApiTokenUseCase: SaveApiTokenUseCase,
    private val deleteApiTokenUseCase: DeleteApiTokenUseCase,
) : ViewModel() {

    // トークン入力欄・ローディング・バリデーションエラーなど、ユーザー操作で変わる状態のみ保持する
    private val editState = MutableStateFlow(TokenEditState())

    val hasAccessToken: StateFlow<Boolean> = getApiTokenUseCase.executeAsFlow()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val tokenUiState: StateFlow<TokenSettingsUiState> = combine(
        getApiTokenUseCase.executeAsFlow(),
        editState,
    ) { token, edit ->
        TokenSettingsUiState(
            tokenInput = edit.tokenInput,
            hasToken = token != null,
            isConnected = token != null,
            isLoading = edit.isLoading,
            validationError = edit.validationError,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TokenSettingsUiState(),
    )

    fun updateTokenInput(token: String) {
        editState.value = editState.value.copy(
            tokenInput = token,
            validationError = null,
        )
    }

    fun saveToken() {
        val currentEdit = editState.value
        if (currentEdit.tokenInput.isBlank()) {
            editState.value = currentEdit.copy(
                validationError = TokenValidationError.TOKEN_EMPTY,
            )
            return
        }

        viewModelScope.launch {
            editState.value = currentEdit.copy(isLoading = true, validationError = null)

            val saveResult = saveApiTokenUseCase.execute(currentEdit.tokenInput)
            // 保存に成功するとトークンFlowが再emitされ、hasToken/isConnectedは自動更新される
            if (saveResult.isSuccess) {
                editState.value = TokenEditState()
            } else {
                val validationError = when (saveResult.exceptionOrNull()) {
                    is TokenError.InvalidFormat -> TokenValidationError.INVALID_TOKEN_FORMAT
                    is TokenError.EmptyToken -> TokenValidationError.TOKEN_EMPTY
                    else -> TokenValidationError.FAILED_TO_SAVE_TOKEN
                }
                // currentEditではなく最新のeditStateを基準に更新し、保存中にユーザーが入力した
                // 新しいトークンを古い値で上書きしないようにする。
                editState.update {
                    it.copy(isLoading = false, validationError = validationError)
                }
            }
        }
    }

    fun deleteToken() {
        viewModelScope.launch {
            editState.update { it.copy(isLoading = true) }

            try {
                deleteApiTokenUseCase.execute()
                // 削除でトークンFlowが再emitされ、hasToken/isConnectedは自動更新される
                editState.value = TokenEditState()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 削除に失敗してもローディングは必ず解除し、画面が固まらないようにする
                editState.update { it.copy(isLoading = false) }
            }
        }
    }
}

private data class TokenEditState(
    val tokenInput: String = "",
    val isLoading: Boolean = false,
    val validationError: TokenValidationError? = null,
)

data class TokenSettingsUiState(
    val tokenInput: String = "",
    val hasToken: Boolean = false,
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val validationError: TokenValidationError? = null,
)

enum class TokenValidationError {
    TOKEN_EMPTY,
    INVALID_TOKEN_FORMAT,
    FAILED_TO_SAVE_TOKEN,
}
