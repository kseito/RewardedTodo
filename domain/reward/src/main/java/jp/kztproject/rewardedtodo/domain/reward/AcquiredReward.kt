package jp.kztproject.rewardedtodo.domain.reward

data class AcquiredReward(
    val id: Id,
    val rewardId: RewardId,
    val name: RewardName,
    val description: RewardDescription,
    val acquiredAt: String,
    val usedAt: String?,
) {
    @JvmInline
    value class Id(val value: String)
}
