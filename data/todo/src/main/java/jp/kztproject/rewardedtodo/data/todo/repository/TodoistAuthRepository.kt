package jp.kztproject.rewardedtodo.data.todo.repository

import jp.kztproject.rewardedtodo.data.todoist.TodoistAuthApi
import jp.kztproject.rewardedtodo.data.todoist.model.TokenResponse
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.AuthorizationCode
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.CurrentTimeProvider
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TodoistOAuthConfig
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoistAuthRepository @Inject constructor(
    private val todoistAuthApi: TodoistAuthApi,
    private val config: TodoistOAuthConfig,
    private val currentTimeProvider: CurrentTimeProvider,
) : ITodoistAuthRepository {

    override suspend fun exchangeCodeForCredential(
        code: AuthorizationCode,
        codeVerifier: CodeVerifier,
    ): Result<TodoistCredential> = runCatchingApi {
        todoistAuthApi.exchangeAuthorizationCode(
            clientId = config.clientId,
            grantType = TodoistAuthApi.GRANT_TYPE_AUTHORIZATION_CODE,
            code = code.value,
            redirectUri = config.redirectUri,
            codeVerifier = codeVerifier.value,
        ).toCredential()
    }

    override suspend fun refreshCredential(refreshToken: RefreshToken): Result<TodoistCredential> = runCatchingApi {
        todoistAuthApi.refreshAccessToken(
            clientId = config.clientId,
            grantType = TodoistAuthApi.GRANT_TYPE_REFRESH_TOKEN,
            refreshToken = refreshToken.value,
        ).toCredential()
    }

    private suspend fun <T> runCatchingApi(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TokenResponse.toCredential(): TodoistCredential = TodoistCredential(
        accessToken = ApiToken.create(accessToken),
        // グレース期間内の再試行では省略される。呼び出し側が既存の値とmergeする
        refreshToken = RefreshToken.createSafely(refreshToken),
        // expires_inは相対秒。判定を単純にするため絶対時刻に変換して保持する
        expiresAt = expiresIn?.let { currentTimeProvider.nowMillis() + it * MILLIS_PER_SECOND },
    )

    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
    }
}
