package jp.kztproject.rewardedtodo.data.reward.network

import com.squareup.moshi.Json

data class SaveRewardRequest(
    val name: String,
    val description: String?,
    @Json(name = "probability_bps") val probabilityBps: Int,
    @Json(name = "need_repeat") val needRepeat: Boolean,
    val version: Int?,
)
