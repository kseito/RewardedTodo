package jp.kztproject.rewardedtodo.data.reward.network

import com.squareup.moshi.Json
import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName

data class AcquiredRewardResponse(
    val id: String,
    @Json(name = "reward_definition_id") val rewardId: Int,
    val name: String,
    val description: String?,
    @Json(name = "acquired_at") val acquiredAt: String,
    @Json(name = "used_at") val usedAt: String?,
) {
    fun domain() = AcquiredReward(
        AcquiredReward.Id(id),
        RewardId(rewardId),
        RewardName(name),
        RewardDescription(description),
        acquiredAt,
        usedAt,
    )
}
