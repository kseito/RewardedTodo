package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

object LotteryBoxFactory {

    fun create(rewardEntities: List<RewardEntity>): LotteryBox {
        val tickets = mutableListOf<Ticket>()
        rewardEntities.forEach {
            val numOfTicket = (100 * it.probability).toInt()
            repeat(numOfTicket) { _ -> tickets.add(Ticket.Prize(it.id)) }
        }
        while(tickets.size < Ticket.ISSUE_LIMIT) {
            tickets.add(Ticket.Miss)
        }
        return LotteryBox(tickets)
    }
}