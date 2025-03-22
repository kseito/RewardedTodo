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

class LotteryInteractorTest {

    private val mockTicketRepository: ITicketRepository = mockk()

    private val interactor = LotteryInteractor(mockTicketRepository)

    @Test
    fun shouldGetPrize() = runTest {
        coEvery { mockTicketRepository.consumeTicket() } returns Unit

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
        val response = interactor.execute(rewards).getOrNull()!!
        assertThat(response.rewardId).isEqualTo(RewardId(1))
        coVerify(exactly = 1) { mockTicketRepository.consumeTicket() }
    }

    @Test
    fun shouldMissPrize() = runTest {
        coEvery { mockTicketRepository.consumeTicket() } returns Unit

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
        val response = interactor.execute(rewards).getOrNull()
        assertThat(response).isNull()
    }

    @Test
    fun shouldOccurErrorWhenRewardPointIsZero() = runTest {
        coEvery { mockTicketRepository.consumeTicket() } throws LackOfTicketsException()

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
        val response = interactor.execute(rewards)
        assertThat(response.exceptionOrNull()).isInstanceOf(LackOfTicketsException::class.java)
    }
}
