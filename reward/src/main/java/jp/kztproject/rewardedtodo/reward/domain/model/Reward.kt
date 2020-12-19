package jp.kztproject.rewardedtodo.reward.domain.model

data class Reward(
        val rewardId: RewardId,
        val name: RewardName,
        val consumePoint: Int,
        val probability: Probability,
        val description: RewardDescription,
        val needRepeat: Boolean
) {
    fun getName() = name.value
    fun getDescription() = description.value
}