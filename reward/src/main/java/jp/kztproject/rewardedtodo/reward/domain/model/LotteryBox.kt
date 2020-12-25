package jp.kztproject.rewardedtodo.reward.domain.model

import jp.kztproject.rewardedtodo.domain.reward.Ticket

class LotteryBox(val tickets: List<Ticket>) {

    fun draw(ticketNumber: Int): Ticket {
        return tickets[ticketNumber]
    }
}