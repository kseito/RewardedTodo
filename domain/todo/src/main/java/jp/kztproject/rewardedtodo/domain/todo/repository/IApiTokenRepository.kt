package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import kotlinx.coroutines.flow.Flow

interface IApiTokenRepository {

    suspend fun saveToken(token: ApiToken): Result<Unit>

    suspend fun getToken(): ApiToken?

    fun getTokenAsFlow(): Flow<ApiToken?>

    suspend fun deleteToken()

    suspend fun hasToken(): Boolean
}
