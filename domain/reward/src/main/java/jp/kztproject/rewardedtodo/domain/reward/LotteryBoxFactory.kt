package jp.kztproject.rewardedtodo.domain.reward

object LotteryBoxFactory {

    fun create(rewards: RewardCollection): LotteryBox {
        val tickets = rewards.createTickets()
        return LotteryBox(tickets)
    }
}