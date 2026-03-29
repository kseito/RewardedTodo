package jp.kztproject.rewardedtodo.data.ticket.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserMeResponse(@Json(name = "user_id") val userId: String)
