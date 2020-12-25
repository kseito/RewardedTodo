package jp.kztproject.rewardedtodo.reward

import jp.kztproject.rewardedtodo.reward.domain.model.*

object TestRewardCreator {

    fun createOne(): List<Reward> {
        return listOf(
                Reward(
                        RewardId(1),
                        RewardName("reward1"),
                        1,
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
                        1,
                        Probability(50F),
                        RewardDescription(null),
                        false),

                Reward(
                        RewardId(2),
                        RewardName("reward2"),
                        1,
                        Probability(1F),
                        RewardDescription(null),
                        false),
                Reward(
                        RewardId(3),
                        RewardName("reward3"),
                        1,
                        Probability(0.01F),
                        RewardDescription(null),
                        false)
        )
    }
}