package kztproject.jp.splacounter.reward.presentation.detail.model

import kztproject.jp.splacounter.reward.domain.model.*

data class RewardInput(
        var id: Int? = null,
        var name: String? = null,
        var consumePoint: Int? = null,
        var probability: Float? = null,
        var description: String? = null,
        var needRepeat: Boolean = false
) {

    companion object {
        fun from(reward: Reward): RewardInput {
            return RewardInput(
                    reward.rewardId.value,
                    reward.name.value,
                    reward.consumePoint,
                    reward.probability.value,
                    reward.description.value,
                    reward.needRepeat
            )
        }
    }
}