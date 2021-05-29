package jp.kztproject.rewardedtodo.data.auth

interface ITodoistAccessTokenRepository {
    suspend fun refresh(clientId: String, clientToken: String, code: String)

    suspend fun get(): String

    suspend fun clear()
}