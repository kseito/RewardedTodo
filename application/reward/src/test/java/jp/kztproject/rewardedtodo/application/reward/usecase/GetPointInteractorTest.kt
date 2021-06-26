package jp.kztproject.rewardedtodo.application.reward.usecase

import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointInteractor
import kotlinx.coroutines.runBlocking
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import org.junit.Test

class GetPointInteractorTest {

    private val mockTicketRepository: ITicketRepository = mock()

    @Test
    fun shouldGetPoint() {
        runBlocking {
            val interactor = GetPointInteractor(mockTicketRepository)
            interactor.execute()
            verify(mockTicketRepository, times(1)).getNumberOfTicket()
        }
    }
}