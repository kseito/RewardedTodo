package kztproject.jp.splacounter.data.ticket

interface ITicketRepository {
    fun addTicket(numberOfTicket: Float)

    fun getNumberOfTicket(): Float
}