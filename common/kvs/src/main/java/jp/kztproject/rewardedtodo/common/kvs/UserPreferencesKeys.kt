package jp.kztproject.rewardedtodo.common.kvs

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {

    val TODOIST_ACCESS_TOKEN = stringPreferencesKey("todoist_access_token")

    val TODOIST_REFRESH_TOKEN = stringPreferencesKey("todoist_refresh_token")

    val TODOIST_TOKEN_EXPIRES_AT = longPreferencesKey("todoist_token_expires_at")

    val REWARD_USER_ID = stringPreferencesKey("reward_user_id")

    /** 端末に残っているチケット残数。連携アカウントの切り替え時に削除する。 */
    val NUMBER_OF_TICKET = intPreferencesKey("number_of_ticket")
}
