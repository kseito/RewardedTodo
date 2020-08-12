package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

object TestRewardCreator {

    fun createOne(): List<RewardEntity> {
        return listOf(
                RewardEntity(1, "reward1", 1, 50F, null, false)
        )
    }

    fun createThree(): List<RewardEntity> {
        return listOf(
                RewardEntity(1, "reward1", 1, 50F, null, false),
                RewardEntity(2, "reward2", 1, 1F, null, false),
                RewardEntity(3, "reward3", 1, 0.01F, null, false)
        )
    }
}