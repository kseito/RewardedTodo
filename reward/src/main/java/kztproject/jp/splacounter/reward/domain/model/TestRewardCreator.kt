package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.database.model.Reward

object TestRewardCreator {

    fun createOne(): List<Reward> {
        return listOf(
                Reward(1, "reward1", 1, 50F, null, false)
        )
    }
}