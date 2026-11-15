package jp.kztproject.rewardedtodo.domain.reward.repository

import kotlinx.coroutines.flow.Flow

interface ITicketRepository {
    suspend fun addTicket(numberOfTicket: Int)

    // TODO change return type to domain model
    suspend fun getNumberOfTicket(): Flow<Int>
}
