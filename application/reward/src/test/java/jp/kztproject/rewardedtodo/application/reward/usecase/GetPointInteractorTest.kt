package jp.kztproject.rewardedtodo.application.reward.usecase

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import org.junit.Test

class GetPointInteractorTest {

    private val mockTicketRepository: ITicketRepository = mockk(relaxed = true)

    @Test
    fun shouldGetPoint() = runTest {
        val interactor = GetPointInteractor(mockTicketRepository)
        interactor.execute()

        coVerify(exactly = 1) { mockTicketRepository.getNumberOfTicket() }
    }
}
