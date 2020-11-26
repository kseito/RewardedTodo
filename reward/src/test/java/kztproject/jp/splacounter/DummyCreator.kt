package kztproject.jp.splacounter

import kztproject.jp.splacounter.reward.domain.model.*
import kztproject.jp.splacounter.reward.infrastructure.api.model.RewardUser
import kztproject.jp.splacounter.reward.infrastructure.database.model.NumberOfTicket

object DummyCreator {

    fun createDummyRewardUser(): RewardUser {
        return RewardUser(10, 123, 0)
    }

    fun createDummyRewardInput(): RewardInput {
        return RewardInput(1, "Test", 5, 10F, "Test description", true)
    }

    fun createDummyRewardPoint(): NumberOfTicket {
        return NumberOfTicket(10)
    }

    fun createDummyRewards(): List<Reward> {
        return listOf(
                createDummyReward()
        )
    }

    fun createDummyReward(): Reward {
        return Reward(RewardId(1),
                RewardName("Test"),
                5,
                Probability(10F),
                RewardDescription("Test description"),
                true)
    }

    fun createDummyNoRepeatReward(): Reward {
        return Reward(RewardId(2),
                RewardName("Test2"),
                7,
                Probability(20F),
                RewardDescription("Test description"),
                false)
    }
}
