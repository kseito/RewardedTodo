package jp.kztproject.rewardedtodo.data.todo

import android.annotation.SuppressLint
import android.content.SharedPreferences
import jp.kztproject.rewardedtodo.common.kvs.EncryptedStore
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import javax.inject.Inject
import javax.inject.Named
import androidx.core.content.edit

@SuppressLint("ApplySharedPref")
class ApiTokenRepository @Inject constructor(@param:Named("encrypted") private val preferences: SharedPreferences) :
    IApiTokenRepository {

    override suspend fun saveToken(token: ApiToken): Result<Unit> {
        preferences.edit(commit = true) {
            putString(EncryptedStore.TODOIST_API_TOKEN, token.value)
        }
        return Result.success(Unit)
    }

    override suspend fun getToken(): ApiToken? {
        val tokenValue = preferences.getString(EncryptedStore.TODOIST_API_TOKEN, null)
        return ApiToken.createSafely(tokenValue)
    }

    override suspend fun deleteToken() {
        preferences.edit(commit = true) {
            remove(EncryptedStore.TODOIST_API_TOKEN)
        }
    }

    override suspend fun hasToken(): Boolean = preferences.contains(EncryptedStore.TODOIST_API_TOKEN) &&
        !preferences.getString(EncryptedStore.TODOIST_API_TOKEN, "").isNullOrBlank()
}
