package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

class RewardCollection {

    companion object {
        fun convertFrom(rewardEntities: List<RewardEntity>): List<Reward> {
            return rewardEntities.map { it.convert() }
        }
    }
}