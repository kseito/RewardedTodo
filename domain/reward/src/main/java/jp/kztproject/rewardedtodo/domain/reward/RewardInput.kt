package jp.kztproject.rewardedtodo.domain.reward

data class RewardInput(
    var id: Int? = null,
    var name: String? = null,
    var probability: Float? = null,
    var description: String? = null,
    var needRepeat: Boolean = false,
    var version: Int? = null,
) {

    companion object {
        fun from(reward: Reward): RewardInput = RewardInput(
            reward.rewardId.value,
            reward.name.value,
            reward.probability.value,
            reward.description.value,
            reward.needRepeat,
            reward.version,
        )
    }
}
