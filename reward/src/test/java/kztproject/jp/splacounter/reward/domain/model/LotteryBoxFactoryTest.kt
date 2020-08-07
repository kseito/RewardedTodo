package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.database.model.Reward
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
}