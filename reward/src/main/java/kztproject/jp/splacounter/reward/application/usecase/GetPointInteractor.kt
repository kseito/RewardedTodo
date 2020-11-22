package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.data.ticket.ITicketRepository
import kztproject.jp.splacounter.reward.infrastructure.database.model.NumberOfTicket
import javax.inject.Inject

class GetPointInteractor @Inject constructor(
        private val ticketRepository: ITicketRepository
) : GetPointUseCase {
    override suspend fun execute(): NumberOfTicket {
        val numberOfTicket = ticketRepository.getNumberOfTicket().toInt()
        return NumberOfTicket(numberOfTicket)
    }

}