package jp.kztproject.rewardedtodo.data.reward.network

import com.squareup.moshi.Json

data class AcquiredRewardPageResponse(
    val items: List<AcquiredRewardResponse>,
    @Json(name = "next_cursor") val nextCursor: String?,
)
