package jp.kztproject.rewardedtodo.domain.reward

data class Reward(
    val rewardId: RewardId,
    val name: RewardName,
    val probability: Probability,
    val description: RewardDescription,
    val needRepeat: Boolean,
    val version: Int = 1,
) {
    fun getName() = name.value
    fun getDescription() = description.value
}
