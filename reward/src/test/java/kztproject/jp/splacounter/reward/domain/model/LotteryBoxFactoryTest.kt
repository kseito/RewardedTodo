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
        val rewards = listOf(Reward(1, "reward1", 1, 50F, null, false))
        val lotteryBox = LotteryBoxFactory.create(rewards)
        val prizeTickets = lotteryBox.tickets.filterIsInstance<Ticket.Prize>()
        assertThat(prizeTickets.size).isEqualTo(5000)
    }
}