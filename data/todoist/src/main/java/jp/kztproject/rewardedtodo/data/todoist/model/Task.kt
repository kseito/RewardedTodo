package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

data class Task(
    val id: Long,
    val content: String,
    @Json(name = "is_completed") val completed: Boolean,
    val due: Due
)
