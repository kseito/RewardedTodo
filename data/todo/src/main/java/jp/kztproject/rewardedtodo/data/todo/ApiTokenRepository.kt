package jp.kztproject.rewardedtodo.data.todo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ApiTokenRepository @Inject constructor(private val dataStore: DataStore<Preferences>) : IApiTokenRepository {

    override suspend fun saveToken(token: ApiToken): Result<Unit> {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.TODOIST_API_TOKEN] = token.value
        }
        return Result.success(Unit)
    }

    override suspend fun getToken(): ApiToken? {
        val tokenValue = dataStore.data
            .map { it[UserPreferencesKeys.TODOIST_API_TOKEN] }
            .first()
        return ApiToken.createSafely(tokenValue)
    }

    override suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.TODOIST_API_TOKEN)
        }
    }

    override suspend fun hasToken(): Boolean {
        val tokenValue = dataStore.data
            .map { it[UserPreferencesKeys.TODOIST_API_TOKEN] }
            .first()
        return !tokenValue.isNullOrBlank()
    }
}
