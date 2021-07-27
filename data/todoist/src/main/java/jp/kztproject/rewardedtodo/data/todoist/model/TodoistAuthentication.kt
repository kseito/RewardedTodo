package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//@JsonClass(generateAdapter = true)
data class TodoistAuthentication(
        @Json(name = "access_token") val accessToken: String
)