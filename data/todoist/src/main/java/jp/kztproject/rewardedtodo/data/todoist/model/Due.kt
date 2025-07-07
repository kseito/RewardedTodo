package jp.kztproject.rewardedtodo.data.todoist.model

import com.squareup.moshi.Json

data class Due(@param:Json(name = "is_recurring") val recurring: Boolean)
