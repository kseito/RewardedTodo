package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

object LotteryBoxFactory {

    fun create(rewardEntities: List<Reward>): LotteryBox {
        val tickets = mutableListOf<Ticket>()
        rewardEntities.forEach {
            val numOfTicket = (100 * it.probability.value).toInt()
            repeat(numOfTicket) { _ -> tickets.add(Ticket.Prize(it.rewardId.value)) }
        }
        while(tickets.size < Ticket.ISSUE_LIMIT) {
            tickets.add(Ticket.Miss)
        }
        return LotteryBox(tickets)
    }
}