package jp.kztproject.rewardedtodo.data.todo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.repository.IApiTokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// トークンは平文DataStoreで保存している（暗号化しない）。Android 10+のFBEでat-rest暗号化されるため
// 非公開・自分専用アプリの脅威モデルでは追加暗号化は不要と判断。詳細は docs/adr/0001-plaintext-token-storage.md
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

    override fun getTokenAsFlow(): Flow<ApiToken?> = dataStore.data
        // DataStore読み取り時のIOExceptionでFlowが終了し、購読側のトークン状態更新が
        // 止まるのを防ぐ。IO以外の例外はそのまま伝播させる。
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { ApiToken.createSafely(it[UserPreferencesKeys.TODOIST_API_TOKEN]) }

    override suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.TODOIST_API_TOKEN)
        }
    }

    override suspend fun hasToken(): Boolean = !getToken()?.value.isNullOrBlank()
}
