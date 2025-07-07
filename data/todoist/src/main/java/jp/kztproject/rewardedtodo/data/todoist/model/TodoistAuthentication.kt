package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

// @JsonClass(generateAdapter = true)
data class TodoistAuthentication(@param:Json(name = "access_token") val accessToken: String)
