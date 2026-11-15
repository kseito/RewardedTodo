package jp.kztproject.rewardedtodo.common.rewardserver.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PointsResponse(
    @Json(name = "user_id") val userId: String,
    @Json(name = "total_points") val totalPoints: Int,
    @Json(name = "available_points") val availablePoints: Int,
    @Json(name = "task_count") val taskCount: Int,
)
