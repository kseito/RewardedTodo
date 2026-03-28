package jp.kztproject.rewardedtodo.data.ticket

import android.annotation.SuppressLint
import android.content.SharedPreferences
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.data.ticket.network.RewardServerApi
import javax.inject.Inject
import javax.inject.Named

@SuppressLint("ApplySharedPref")
class RewardUserIdRepository @Inject constructor(
    private val api: RewardServerApi,
    @param:Named("encrypted") private val preferences: SharedPreferences,
) {
    companion object {
        private const val KEY_REWARD_USER_ID = "reward_user_id"
    }

    fun getToken(): String = preferences.getString(EncryptedStore.TODOIST_API_TOKEN, "")!!

    suspend fun getUserId(): String {
        val cached = preferences.getString(KEY_REWARD_USER_ID, null)
        if (cached != null) return cached

        val response = api.resolveUserId("Bearer ${getToken()}")
        preferences.edit()
            .putString(KEY_REWARD_USER_ID, response.userId)
            .commit()
        return response.userId
    }

    fun clearUserId() {
        preferences.edit().remove(KEY_REWARD_USER_ID).commit()
    }
}
