package jp.kztproject.rewardedtodo.data.todo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

// トークンは平文DataStoreで保存している（暗号化しない）。Android 10+のFBEでat-rest暗号化されるため
// 非公開・自分専用アプリの脅威モデルでは追加暗号化は不要と判断。詳細は docs/adr/0001-plaintext-token-storage.md
class TodoistCredentialRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ITodoistCredentialRepository {

    override suspend fun saveCredential(credential: TodoistCredential) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.TODOIST_ACCESS_TOKEN] = credential.accessToken.value
            credential.refreshToken
                ?.let { preferences[UserPreferencesKeys.TODOIST_REFRESH_TOKEN] = it.value }
                ?: preferences.remove(UserPreferencesKeys.TODOIST_REFRESH_TOKEN)
            credential.expiresAt
                ?.let { preferences[UserPreferencesKeys.TODOIST_TOKEN_EXPIRES_AT] = it }
                ?: preferences.remove(UserPreferencesKeys.TODOIST_TOKEN_EXPIRES_AT)
        }
    }

    override suspend fun getCredential(): TodoistCredential? = getCredentialAsFlow().first()

    override fun getCredentialAsFlow(): Flow<TodoistCredential?> = dataStore.data
        // DataStore読み取り時のIOExceptionでFlowが終了し、購読側のトークン状態更新が
        // 止まるのを防ぐ。IO以外の例外はそのまま伝播させる。
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences -> preferences.toCredential() }

    override suspend fun deleteCredential() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.TODOIST_ACCESS_TOKEN)
            preferences.remove(UserPreferencesKeys.TODOIST_REFRESH_TOKEN)
            preferences.remove(UserPreferencesKeys.TODOIST_TOKEN_EXPIRES_AT)
        }
    }

    private fun Preferences.toCredential(): TodoistCredential? {
        val accessToken = ApiToken.createSafely(this[UserPreferencesKeys.TODOIST_ACCESS_TOKEN]) ?: return null
        return TodoistCredential(
            accessToken = accessToken,
            refreshToken = RefreshToken.createSafely(this[UserPreferencesKeys.TODOIST_REFRESH_TOKEN]),
            expiresAt = this[UserPreferencesKeys.TODOIST_TOKEN_EXPIRES_AT],
        )
    }
}
