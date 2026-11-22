package jp.kztproject.rewardedtodo.data.reward.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryAnimationPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotteryAnimationPreferenceRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ILotteryAnimationPreferenceRepository {
    override fun observeEnabled(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.LOTTERY_ANIMATION_ENABLED] ?: true
    }

    override suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.LOTTERY_ANIMATION_ENABLED] = enabled
        }
    }
}
