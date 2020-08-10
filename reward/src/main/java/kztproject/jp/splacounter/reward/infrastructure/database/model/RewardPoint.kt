package kztproject.jp.splacounter.reward.infrastructure.database.model

import com.google.gson.annotations.SerializedName

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class RewardPoint(@SerializedName("point") val value: Int)