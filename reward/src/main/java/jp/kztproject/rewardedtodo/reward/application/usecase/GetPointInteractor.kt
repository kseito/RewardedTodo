package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.reward.infrastructure.database.model.NumberOfTicket
import javax.inject.Inject

class GetPointInteractor @Inject constructor(
        private val ticketRepository: ITicketRepository
) : GetPointUseCase {
    override suspend fun execute(): NumberOfTicket {
        val numberOfTicket = ticketRepository.getNumberOfTicket().toInt()
        return NumberOfTicket(numberOfTicket)
    }

}