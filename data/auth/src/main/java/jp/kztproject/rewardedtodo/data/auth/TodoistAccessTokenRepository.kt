package jp.kztproject.rewardedtodo.data.auth

import javax.inject.Inject

class TodoistAccessTokenRepository @Inject constructor(
        private val api: TodoistApi
) : ITodoistAccessTokenRepository {

    override suspend fun fetch(clientId: String, clientToken: String, code: String): String {
        return api.fetchAccessToken(clientId, clientToken, code).accessToken
    }
}