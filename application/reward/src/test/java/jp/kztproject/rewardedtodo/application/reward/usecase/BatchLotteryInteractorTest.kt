package jp.kztproject.rewardedtodo.application.reward.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BatchLotteryInteractorTest {

    private val mockTicketRepository: ITicketRepository = mockk()

    private val interactor = BatchLotteryInteractor(mockTicketRepository)

    @Test
    fun `returns all wins when probability is 100 percent`() = runTest {
        coEvery { mockTicketRepository.consumeTickets(any()) } returns Unit

        val rewards = RewardCollection(
            listOf(
                Reward(
                    RewardId(1),
                    RewardName("reward1"),
                    Probability(100F),
                    RewardDescription(null),
                    true,
                ),
            ),
        )
        val result = interactor.execute(rewards, 3)

        assertThat(result.isSuccess).isTrue()
        val batchResult = result.getOrNull()!!
        assertThat(batchResult.wonRewards).hasSize(3)
        assertThat(batchResult.missCount).isEqualTo(0)
        coVerify(exactly = 1) { mockTicketRepository.consumeTickets(3) }
    }

    @Test
    fun `returns all misses when probability is 0 percent`() = runTest {
        coEvery { mockTicketRepository.consumeTickets(any()) } returns Unit

        val rewards = RewardCollection(
            listOf(
                Reward(
                    RewardId(1),
                    RewardName("reward1"),
                    Probability(0F),
                    RewardDescription(null),
                    true,
                ),
            ),
        )
        val result = interactor.execute(rewards, 3)

        assertThat(result.isSuccess).isTrue()
        val batchResult = result.getOrNull()!!
        assertThat(batchResult.wonRewards).isEmpty()
        assertThat(batchResult.missCount).isEqualTo(3)
    }

    @Test
    fun `returns failure when tickets are insufficient`() = runTest {
        coEvery { mockTicketRepository.consumeTickets(any()) } throws LackOfTicketsException()

        val rewards = RewardCollection(
            listOf(
                Reward(
                    RewardId(1),
                    RewardName("reward1"),
                    Probability(100F),
                    RewardDescription(null),
                    true,
                ),
            ),
        )
        val result = interactor.execute(rewards, 5)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(LackOfTicketsException::class.java)
    }
}
