package jp.kztproject.rewardedtodo.data.auth

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Named

class TodoistAccessTokenRepository @Inject constructor(
        private val api: TodoistApi,
        @Named("encrypted") private val preferences: SharedPreferences
) : ITodoistAccessTokenRepository {

    override suspend fun fetch(clientId: String, clientToken: String, code: String): String {
        return api.fetchAccessToken(clientId, clientToken, code).accessToken
    }
}