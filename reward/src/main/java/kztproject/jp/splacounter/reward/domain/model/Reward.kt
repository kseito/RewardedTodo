package kztproject.jp.splacounter.reward.domain.model

data class Reward(
        val rewardId: RewardId,
        val name: String,
        val consumePoint: Int,
        val probability: Probability,
        val description: String?,
        val needRepeat: Boolean
)