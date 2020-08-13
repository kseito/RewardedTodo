package kztproject.jp.splacounter.reward.domain.model

object TestRewardCreator {

    fun createOne(): List<Reward> {
        return listOf(
                Reward(
                        RewardId(1),
                        RewardName("reward1"),
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
                        RewardName("reward1"),
                        1,
                        Probability(50F),
                        null,
                        false),

                Reward(
                        RewardId(2),
                        RewardName("reward2"),
                        1,
                        Probability(1F),
                        null,
                        false),
                Reward(
                        RewardId(3),
                        RewardName("reward3"),
                        1,
                        Probability(0.01F),
                        null,
                        false)
        )
    }
}