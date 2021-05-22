package jp.kztproject.rewardedtodo.data.auth

interface ITodoistAccessTokenRepository {
    suspend fun fetch(clientId: String, clientToken: String, code: String): String
}