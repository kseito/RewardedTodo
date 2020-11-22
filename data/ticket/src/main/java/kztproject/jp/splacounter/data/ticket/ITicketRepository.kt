package kztproject.jp.splacounter.data.ticket

interface ITicketRepository {
    fun addTicket(numberOfTicket: Float)

    //TODO change return type to domain model
    fun getNumberOfTicket(): Float
}