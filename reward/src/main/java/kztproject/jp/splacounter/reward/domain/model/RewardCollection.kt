package kztproject.jp.splacounter.reward.domain.model

import kztproject.jp.splacounter.reward.infrastructure.database.model.RewardEntity

class RewardCollection(private val rewards: List<Reward>) {

    //TODO TicketCollectionの責務にした方が良さそう
    fun createTickets(): List<Ticket> {
        val tickets = mutableListOf<Ticket>()
        rewards.forEach {
            val numOfTicket = (100 * it.probability.value).toInt()
            repeat(numOfTicket) { _ -> tickets.add(Ticket.Prize(it.rewardId.value)) }
        }
        while (tickets.size < Ticket.ISSUE_LIMIT) {
            tickets.add(Ticket.Miss)
        }
        return tickets
    }

    fun findBy(id: Int): Reward {
        return rewards.first { it.rewardId.value == id }
    }

    companion object {
        fun convertFrom(rewardEntities: List<RewardEntity>): RewardCollection {
            val rewards = rewardEntities.map { it.convert() }
            return RewardCollection(rewards)
        }
    }
}