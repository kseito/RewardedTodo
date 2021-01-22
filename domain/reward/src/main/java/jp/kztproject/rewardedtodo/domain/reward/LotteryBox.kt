package jp.kztproject.rewardedtodo.domain.reward

class LotteryBox(val tickets: List<Ticket>) {

    fun draw(ticketNumber: Int): Ticket {
        return tickets[ticketNumber]
    }
}