package jp.kztproject.rewardedtodo

import jp.kztproject.rewardedtodo.domain.reward.*

object DummyCreator {

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
