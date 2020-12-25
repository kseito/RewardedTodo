package jp.kztproject.rewardedtodo.reward.infrastructure.database.model

import com.google.gson.annotations.SerializedName

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class NumberOfTicket(@SerializedName("point") val value: Int)