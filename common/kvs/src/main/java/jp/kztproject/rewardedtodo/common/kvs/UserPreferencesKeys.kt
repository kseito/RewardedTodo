package jp.kztproject.rewardedtodo.common.kvs

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {

    val TODOIST_ACCESS_TOKEN = stringPreferencesKey("todoist_access_token")

    val TODOIST_REFRESH_TOKEN = stringPreferencesKey("todoist_refresh_token")

    val TODOIST_TOKEN_EXPIRES_AT = longPreferencesKey("todoist_token_expires_at")

    val TODOIST_AUTH_STATE = stringPreferencesKey("todoist_auth_state")

    val TODOIST_AUTH_CODE_VERIFIER = stringPreferencesKey("todoist_auth_code_verifier")

    val REWARD_USER_ID = stringPreferencesKey("reward_user_id")
}
