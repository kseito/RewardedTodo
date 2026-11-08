package jp.kztproject.rewardedtodo.common.rewardserver

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.common.rewardserver.network.RewardIdentityApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RewardUserIdRepository @Inject constructor(
    private val api: RewardIdentityApi,
    private val dataStore: DataStore<Preferences>,
) {

    suspend fun getUserId(): String {
        val cached = dataStore.data
            .map { it[UserPreferencesKeys.REWARD_USER_ID] }
            .first()
        if (cached != null) return cached

        val response = api.resolveUserId()
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.REWARD_USER_ID] = response.userId
        }
        return response.userId
    }

    suspend fun refreshUserId(expectedUser: String, authorization: String): Boolean {
        val resolved = api.resolveUserId(authorization).userId
        val preferences = dataStore.edit {
            val token = it[UserPreferencesKeys.TODOIST_ACCESS_TOKEN]
            if (token != null && authorization == "Bearer $token") {
                it[UserPreferencesKeys.REWARD_USER_ID] = resolved
            }
        }
        val token = preferences[UserPreferencesKeys.TODOIST_ACCESS_TOKEN]
        return token != null && authorization == "Bearer $token" && resolved == expectedUser
    }
}
