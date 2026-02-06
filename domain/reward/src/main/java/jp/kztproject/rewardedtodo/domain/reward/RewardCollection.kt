package jp.kztproject.rewardedtodo.domain.reward

import kotlin.math.roundToInt

class RewardCollection(private val rewards: List<Reward>) {

    companion object {
        const val MAX = 7
    }

    //TODO TicketCollectionの責務にした方が良さそう
    fun createTickets(): List<Ticket> {
        val tickets = mutableListOf<Ticket>()
        rewards.forEach {
            val numOfTicket = (100 * it.probability.value).roundToInt()
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
}