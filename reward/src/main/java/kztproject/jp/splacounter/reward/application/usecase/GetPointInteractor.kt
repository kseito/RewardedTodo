package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.data.ticket.ITicketRepository
import kztproject.jp.splacounter.reward.application.repository.IPointRepository
import kztproject.jp.splacounter.reward.infrastructure.database.model.NumberOfTicket
import javax.inject.Inject

class GetPointInteractor @Inject constructor(
        private val pointRepository: IPointRepository,
        private val ticketRepository: ITicketRepository
) : GetPointUseCase {
    override suspend fun execute(): NumberOfTicket {
        return pointRepository.loadPoint()
    }

}