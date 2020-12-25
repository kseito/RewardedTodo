package jp.kztproject.rewardedtodo.reward.domain.model

import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.Ticket
import jp.kztproject.rewardedtodo.reward.infrastructure.database.model.RewardEntity

class RewardCollection(private val rewards: List<Reward>) {

    //TODO TicketCollectionの責務にした方が良さそう
    fun createTickets(): List<Ticket> {
        val tickets = mutableListOf<Ticket>()
        rewards.forEach {
            val numOfTicket = (100 * it.probability.value).toInt()
            repeat(numOfTicket) { _ -> tickets.add(Ticket.Prize(it.rewardId)) }
        }
        while (tickets.size < Ticket.ISSUE_LIMIT) {
            tickets.add(Ticket.Miss)
        }
        return tickets
    }

    fun findBy(id: RewardId): Reward {
        return rewards.first { it.rewardId == id }
    }

    companion object {
        fun convertFrom(rewardEntities: List<RewardEntity>): RewardCollection {
            val rewards = rewardEntities.map { it.convert() }
            return RewardCollection(rewards)
        }
    }
}