package kztproject.jp.splacounter.reward.domain.model

class LotteryBox(val tickets: List<Ticket>) {

    fun draw(ticketNumber: Int): Ticket {
        return tickets[ticketNumber]
    }
}