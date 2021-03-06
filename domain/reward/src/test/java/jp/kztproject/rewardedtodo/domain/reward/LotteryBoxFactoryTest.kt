package jp.kztproject.rewardedtodo.domain.reward

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LotteryBoxFactoryTest {

    @Test
    fun NoRewards() {
        val actual = LotteryBoxFactory.create(RewardCollection(listOf()))
        assertThat(actual.tickets.size).isEqualTo(10000)
    }

    @Test
    fun OneReward() {
        val rewards = TestRewardCreator.createOne()
        val lotteryBox = LotteryBoxFactory.create(RewardCollection(rewards))
        val prizeTickets = lotteryBox.tickets.filterIsInstance<Ticket.Prize>()
        assertThat(prizeTickets.size).isEqualTo(5000)
        prizeTickets.forEach {
            assertThat(it.rewardId).isEqualTo(RewardId(1))
        }
    }

    @Test
    fun ThreeRewards() {
        val rewards = TestRewardCreator.createThree()

        val lotteryBox = LotteryBoxFactory.create(RewardCollection(rewards))

        val firstRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == RewardId(1) }
        assertThat(firstRewardTickets.size).isEqualTo(5000)
        val secondRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == RewardId(2) }
        assertThat(secondRewardTickets.size).isEqualTo(100)
        val thirdRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == RewardId(3) }
        assertThat(thirdRewardTickets.size).isEqualTo(1)
    }
}