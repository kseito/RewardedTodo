package jp.kztproject.rewardedtodo.data.ticket

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.domain.reward.repository.IAccountCacheRepository
import javax.inject.Inject

class AccountCacheRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    IAccountCacheRepository {

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.NUMBER_OF_TICKET)
            preferences.remove(UserPreferencesKeys.REWARD_USER_ID)
        }
    }
}
