package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPointInteractor @Inject constructor(
        private val ticketRepository: ITicketRepository
) : GetPointUseCase {
    override suspend fun execute(): Flow<NumberOfTicket> {
        return ticketRepository.getNumberOfTicket().map {
            NumberOfTicket(it.toInt())
        }
    }

}
