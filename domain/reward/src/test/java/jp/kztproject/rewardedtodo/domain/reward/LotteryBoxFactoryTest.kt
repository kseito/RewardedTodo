package jp.kztproject.rewardedtodo.domain.reward

import io.kotest.matchers.shouldBe
import org.junit.Test

class LotteryBoxFactoryTest {

    @Test
    fun noRewards() {
        val actual = LotteryBoxFactory.create(RewardCollection(listOf()))
        actual.tickets.size shouldBe 10000
    }

    @Test
    fun oneReward() {
        val rewards = TestRewardCreator.createOne()
        val lotteryBox = LotteryBoxFactory.create(RewardCollection(rewards))
        val prizeTickets = lotteryBox.tickets.filterIsInstance<Ticket.Prize>()
        prizeTickets.size shouldBe 5000
        prizeTickets.forEach {
            it.rewardId shouldBe RewardId(1)
        }
    }

    @Test
    fun threeRewards() {
        val rewards = TestRewardCreator.createThree()

        val lotteryBox = LotteryBoxFactory.create(RewardCollection(rewards))

        val firstRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == RewardId(1) }
        firstRewardTickets.size shouldBe 5000
        val secondRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == RewardId(2) }
        secondRewardTickets.size shouldBe 100
        val thirdRewardTickets = lotteryBox.tickets.filter { it is Ticket.Prize && it.rewardId == RewardId(3) }
        thirdRewardTickets.size shouldBe 1
    }
}
