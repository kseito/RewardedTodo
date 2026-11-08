package jp.kztproject.rewardedtodo.common.rewardserver.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserMeResponse(@Json(name = "user_id") val userId: String)
