package jp.kztproject.rewardedtodo.data.reward.network

import com.squareup.moshi.Json

data class LotteryRequest(@Json(name = "request_id") val requestId: String, val count: Int)
