package jp.kztproject.rewardedtodo.data.ticket

import kotlinx.coroutines.flow.Flow

interface ITicketRepository {
    suspend fun addTicket(numberOfTicket: Float)

    fun consumeTicket()

    //TODO change return type to domain model
    suspend fun getNumberOfTicket(): Flow<Float>
}
