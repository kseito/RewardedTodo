package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.ApiToken

interface IApiTokenRepository {

    suspend fun saveToken(token: ApiToken): Result<Unit>

    suspend fun getToken(): ApiToken?

    suspend fun deleteToken()

    suspend fun hasToken(): Boolean
}
