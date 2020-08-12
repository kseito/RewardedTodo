package kztproject.jp.splacounter.reward.domain.model

object LotteryBoxFactory {

    fun create(rewards: RewardCollection): LotteryBox {
        val tickets = rewards.createTickets()
        return LotteryBox(tickets)
    }
}