package jp.kztproject.rewardedtodo.data.reward.network

import com.squareup.moshi.Json
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName

data class RewardResponse(
    val id: Int,
    val name: String,
    val description: String?,
    @Json(name = "probability_bps") val probabilityBps: Int,
    @Json(name = "need_repeat") val needRepeat: Boolean,
    val version: Int,
) {
    fun domain() = Reward(
        RewardId(id),
        RewardName(name),
        Probability(probabilityBps / 100f),
        RewardDescription(description),
        needRepeat,
        version,
    )
}
