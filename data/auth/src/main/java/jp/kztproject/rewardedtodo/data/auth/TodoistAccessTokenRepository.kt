package jp.kztproject.rewardedtodo.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.data.todoist.TodoistApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoistAccessTokenRepository @Inject constructor(
    private val api: TodoistApi,
    private val dataStore: DataStore<Preferences>,
) : ITodoistAccessTokenRepository {

    override suspend fun refresh(clientId: String, clientToken: String, code: String) {
        val accessToken = api.fetchAccessToken(clientId, clientToken, code).accessToken
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.TODOIST_ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun get(): String = dataStore.data
        .map { it[UserPreferencesKeys.TODOIST_ACCESS_TOKEN].orEmpty() }
        .first()

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.TODOIST_ACCESS_TOKEN)
        }
    }
}
