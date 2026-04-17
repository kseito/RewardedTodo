package jp.kztproject.rewardedtodo.data.ticket

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RewardUserIdRepository @Inject constructor(
    private val api: RewardServerApi,
    private val dataStore: DataStore<Preferences>,
) {

    suspend fun getToken(): String = dataStore.data
        .map { it[UserPreferencesKeys.TODOIST_API_TOKEN].orEmpty() }
        .first()

    suspend fun getUserId(): String {
        val cached = dataStore.data
            .map { it[UserPreferencesKeys.REWARD_USER_ID] }
            .first()
        if (cached != null) return cached

        val response = api.resolveUserId("Bearer ${getToken()}")
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.REWARD_USER_ID] = response.userId
        }
        return response.userId
    }

    suspend fun clearUserId() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.REWARD_USER_ID)
        }
    }
}
