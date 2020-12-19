package jp.kztproject.rewardedtodo.reward.domain.model

class LotteryBox(val tickets: List<Ticket>) {

    fun draw(ticketNumber: Int): Ticket {
        return tickets[ticketNumber]
    }
}