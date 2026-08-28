package jp.kztproject.rewardedtodo.data.todo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import jp.kztproject.rewardedtodo.common.kvs.UserPreferencesKeys
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.OAuthState
import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthSessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 認可リクエスト中のstateとcode_verifierを保持する。
 *
 * Auth Tabの表示中にアプリのプロセスが終了しても認証を継続できるよう、メモリではなく
 * DataStoreに置く。認可コードと引き換えに使い切ったら破棄する。
 */
class TodoistAuthSessionRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ITodoistAuthSessionRepository {

    override suspend fun saveSession(session: TodoistAuthSession) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.TODOIST_AUTH_STATE] = session.state.value
            preferences[UserPreferencesKeys.TODOIST_AUTH_CODE_VERIFIER] = session.codeVerifier.value
        }
    }

    override suspend fun getSession(): TodoistAuthSession? {
        val preferences = dataStore.data.first()
        val state = OAuthState.createSafely(preferences[UserPreferencesKeys.TODOIST_AUTH_STATE]) ?: return null
        val codeVerifier =
            CodeVerifier.createSafely(preferences[UserPreferencesKeys.TODOIST_AUTH_CODE_VERIFIER]) ?: return null
        return TodoistAuthSession(state = state, codeVerifier = codeVerifier)
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.TODOIST_AUTH_STATE)
            preferences.remove(UserPreferencesKeys.TODOIST_AUTH_CODE_VERIFIER)
        }
    }
}
