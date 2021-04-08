package jp.kztproject.rewardedtodo.data.auth

class TodoistAccessTokenRepository(private val api: TodoistApi) {
    fun fetch(clientId: String, clientToken: String, code: String): String {
        return api.fetchAccessToken(clientId, clientToken, code)
    }
}