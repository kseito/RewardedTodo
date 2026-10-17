package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

data class Tasks(val results: List<Task>, @Json(name = "next_cursor") val nextCursor: String? = null)
