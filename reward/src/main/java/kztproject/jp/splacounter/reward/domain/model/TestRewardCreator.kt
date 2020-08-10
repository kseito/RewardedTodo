package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.infrastructure.database.model.Reward

object TestRewardCreator {

    fun createOne(): List<Reward> {
        return listOf(
                Reward(1, "reward1", 1, 50F, null, false)
        )
    }

    fun createThree(): List<Reward> {
        return listOf(
                Reward(1, "reward1", 1, 50F, null, false),
                Reward(2, "reward2", 1, 1F, null, false),
                Reward(3, "reward3", 1, 0.01F, null, false)
        )
    }
}