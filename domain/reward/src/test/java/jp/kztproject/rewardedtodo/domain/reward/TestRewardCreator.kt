package jp.kztproject.rewardedtodo.domain.reward

import jp.kztproject.rewardedtodo.domain.reward.*

object TestRewardCreator {

    fun createOne(): List<Reward> {
        return listOf(
                Reward(
                        RewardId(1),
                        RewardName("reward1"),
                        Probability(50F),
                        RewardDescription(null),
                        false)
        )
    }

    fun createThree(): List<Reward> {
        return listOf(
                Reward(
                        RewardId(1),
                        RewardName("reward1"),
                        Probability(50F),
                        RewardDescription(null),
                        false),

                Reward(
                        RewardId(2),
                        RewardName("reward2"),
                        Probability(1F),
                        RewardDescription(null),
                        false),
                Reward(
                        RewardId(3),
                        RewardName("reward3"),
                        Probability(0.01F),
                        RewardDescription(null),
                        false)
        )
    }
}
