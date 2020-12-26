package jp.kztproject.rewardedtodo.reward.domain.model

import jp.kztproject.rewardedtodo.domain.reward.RewardCollection

object LotteryBoxFactory {

    fun create(rewards: RewardCollection): LotteryBox {
        val tickets = rewards.createTickets()
        return LotteryBox(tickets)
    }
}