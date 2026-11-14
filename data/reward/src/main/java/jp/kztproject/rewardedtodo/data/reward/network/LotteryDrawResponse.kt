package jp.kztproject.rewardedtodo.data.reward.network

import com.squareup.moshi.Json

data class LotteryDrawResponse(val type: String, @Json(name = "acquired_reward") val reward: AcquiredRewardResponse?)
