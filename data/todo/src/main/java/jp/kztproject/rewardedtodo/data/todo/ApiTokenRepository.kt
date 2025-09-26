package jp.kztproject.rewardedtodo.data.todo

import android.annotation.SuppressLint
import android.content.SharedPreferences
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import javax.inject.Inject
import javax.inject.Named

@SuppressLint("ApplySharedPref")
class ApiTokenRepository @Inject constructor(
    @param:Named("encrypted") private val preferences: SharedPreferences,
) : IApiTokenRepository {

    override suspend fun saveToken(token: ApiToken): Result<Unit> {
        preferences.edit()
            .putString(EncryptedStore.TODOIST_API_TOKEN, token.value)
            .commit()
        return Result.success(Unit)
    }

    override suspend fun getToken(): ApiToken? {
        val tokenValue = preferences.getString(EncryptedStore.TODOIST_API_TOKEN, null)
        return tokenValue?.let { ApiToken(it) }
    }

    override suspend fun deleteToken() {
        preferences.edit()
            .remove(EncryptedStore.TODOIST_API_TOKEN)
            .commit()
    }

    override suspend fun hasToken(): Boolean {
        return preferences.contains(EncryptedStore.TODOIST_API_TOKEN) &&
            !preferences.getString(EncryptedStore.TODOIST_API_TOKEN, "").isNullOrBlank()
    }
}