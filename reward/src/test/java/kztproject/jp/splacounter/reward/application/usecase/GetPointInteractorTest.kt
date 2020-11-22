package kztproject.jp.splacounter.reward.application.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import kztproject.jp.splacounter.data.ticket.ITicketRepository
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