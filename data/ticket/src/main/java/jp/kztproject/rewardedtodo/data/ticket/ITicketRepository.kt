package jp.kztproject.rewardedtodo.data.ticket

interface ITicketRepository {
    fun addTicket(numberOfTicket: Float)

    fun consumeTicket()

    //TODO change return type to domain model
    fun getNumberOfTicket(): Float
}