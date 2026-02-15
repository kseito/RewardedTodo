package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

data class Task(val id: String, val content: String, val checked: Boolean, val due: Due?)
