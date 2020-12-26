package jp.kztproject.rewardedtodo.domain.reward

import jp.kztproject.rewardedtodo.domain.reward.Ticket

class LotteryBox(val tickets: List<Ticket>) {

    fun draw(ticketNumber: Int): Ticket {
        return tickets[ticketNumber]
    }
}