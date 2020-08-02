package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.database.model.Reward

object LotteryBoxFactory {

    fun create(rewards: List<Reward>): LotteryBox {
        val tickets = mutableListOf<Ticket>()
        rewards.forEach {
            val numOfTicket = (100 * it.probability).toInt()
            repeat(numOfTicket) { _ -> tickets.add(Ticket.Prize(it.id)) }
        }
        while(tickets.size < 10000) {
            tickets.add(Ticket.Miss)
        }
        return LotteryBox(tickets)
    }
}