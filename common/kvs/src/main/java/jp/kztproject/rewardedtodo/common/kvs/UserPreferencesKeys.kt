package jp.kztproject.rewardedtodo.common.kvs

import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {

    val TODOIST_ACCESS_TOKEN = stringPreferencesKey("todoist_access_token")

    val TODOIST_API_TOKEN = stringPreferencesKey("todoist_api_token")

    val REWARD_USER_ID = stringPreferencesKey("reward_user_id")
}
