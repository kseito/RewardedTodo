package jp.kztproject.rewardedtodo.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.kztproject.rewardedtodo.application.todo.DeleteApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.SaveApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getApiTokenUseCase: GetApiTokenUseCase,
    private val saveApiTokenUseCase: SaveApiTokenUseCase,
    private val deleteApiTokenUseCase: DeleteApiTokenUseCase,
) : ViewModel() {

    private val mutableHasAccessToken = MutableStateFlow(false)
    val hasAccessToken: StateFlow<Boolean> = mutableHasAccessToken

    private val _tokenUiState = MutableStateFlow(TokenSettingsUiState())
    val tokenUiState: StateFlow<TokenSettingsUiState> = _tokenUiState.asStateFlow()

    init {
        loadAccessToken()
        loadCurrentToken()
    }

    fun loadAccessToken() = viewModelScope.launch {
        val hasApiToken = getApiTokenUseCase.execute() != null
        mutableHasAccessToken.value = hasApiToken
    }

    fun updateTokenInput(token: String) {
        _tokenUiState.value = _tokenUiState.value.copy(
            tokenInput = token,
            validationError = null,
        )
    }

    fun saveToken() {
        val currentState = _tokenUiState.value
        if (currentState.tokenInput.isBlank()) {
            _tokenUiState.value = currentState.copy(
                validationError = TokenValidationError.TOKEN_EMPTY,
            )
            return
        }

        viewModelScope.launch {
            _tokenUiState.value = currentState.copy(isLoading = true, validationError = null)

            val saveResult = saveApiTokenUseCase.execute(currentState.tokenInput)
            if (saveResult.isSuccess) {
                _tokenUiState.value = currentState.copy(
                    isLoading = false,
                    hasToken = true,
                    isConnected = true,
                    tokenInput = "",
                    validationError = null,
                )
                loadAccessToken() // Refresh the main token status
            } else {
                val validationError = when (saveResult.exceptionOrNull()) {
                    is TokenError.InvalidFormat -> TokenValidationError.INVALID_TOKEN_FORMAT
                    is TokenError.EmptyToken -> TokenValidationError.TOKEN_EMPTY
                    else -> TokenValidationError.FAILED_TO_SAVE_TOKEN
                }
                _tokenUiState.value = currentState.copy(
                    isLoading = false,
                    validationError = validationError,
                )
            }
        }
    }

    fun deleteToken() {
        viewModelScope.launch {
            _tokenUiState.value = _tokenUiState.value.copy(isLoading = true)

            deleteApiTokenUseCase.execute()

            _tokenUiState.value = _tokenUiState.value.copy(
                isLoading = false,
                hasToken = false,
                isConnected = false,
                tokenInput = "",
                validationError = null,
            )
            loadAccessToken() // Refresh the main token status
        }
    }

    private fun loadCurrentToken() {
        viewModelScope.launch {
            val currentToken = getApiTokenUseCase.execute()
            _tokenUiState.value = _tokenUiState.value.copy(
                hasToken = currentToken != null,
                isConnected = currentToken != null,
            )
        }
    }
}

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
