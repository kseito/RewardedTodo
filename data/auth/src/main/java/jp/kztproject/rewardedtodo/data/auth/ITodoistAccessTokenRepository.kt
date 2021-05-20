package jp.kztproject.rewardedtodo.data.auth

interface ITodoistAccessTokenRepository {
    fun fetch(clientId: String, clientToken: String, code: String): String
}