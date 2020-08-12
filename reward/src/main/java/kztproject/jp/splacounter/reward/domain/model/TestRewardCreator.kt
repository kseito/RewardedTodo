package kztproject.jp.splacounter.reward.domain.model

object TestRewardCreator {

    fun createOne(): List<Reward> {
        return listOf(
                Reward(
                        RewardId(1),
                        "reward1",
                        1,
                        Probability(50F),
                        null,
                        false)
        )
    }

    fun createThree(): List<Reward> {
        return listOf(
                Reward(
                        RewardId(1),
                        "reward1",
                        1,
                        Probability(50F),
                        null,
                        false),

                Reward(
                        RewardId(2),
                        "reward2",
                        1,
                        Probability(1F),
                        null,
                        false),
                Reward(
                        RewardId(3),
                        "reward3",
                        1,
                        Probability(0.01F),
                        null,
                        false)
        )
    }
}