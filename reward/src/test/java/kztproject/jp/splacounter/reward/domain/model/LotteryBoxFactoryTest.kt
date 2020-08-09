package kztproject.jp.splacounter.reward.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LotteryBoxFactoryTest {

    @Test
    fun NoRewards() {
        val actual = LotteryBoxFactory.create(listOf())
        assertThat(actual.tickets.size).isEqualTo(10000)
    }

    @Test
    fun OneReward() {
        val rewards = TestRewardCreator.createOne()
        val lotteryBox = LotteryBoxFactory.create(rewards)
        val prizeTickets = lotteryBox.tickets.filterIsInstance<Ticket.Prize>()
        assertThat(prizeTickets.size).isEqualTo(5000)
        prizeTickets.forEach {
            assertThat(it.rewardId).isEqualTo(1)
        }
    }

    @Test
    fun ThreeRewards() {
        val rewards = TestRewardCreator.createThree()

        val lotteryBox = LotteryBoxFactory.create(rewards)

        val firstRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == 1 }
        assertThat(firstRewardTickets.size).isEqualTo(5000)
        val secondRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == 2 }
        assertThat(secondRewardTickets.size).isEqualTo(100)
        val thirdRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == 3 }
        assertThat(thirdRewardTickets.size).isEqualTo(1)
    }
}