package jp.kztproject.rewardedtodo.data.ticket

import kotlinx.coroutines.flow.Flow

interface ITicketRepository {
    suspend fun addTicket(numberOfTicket: Int)

    suspend fun consumeTicket()

    suspend fun consumeTickets(count: Int)

    // TODO change return type to domain model
    suspend fun getNumberOfTicket(): Flow<Int>
}
