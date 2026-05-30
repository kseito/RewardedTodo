package jp.kztproject.rewardedtodo.application.reward.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.data.ticket.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LotteryInteractorTest {

    private val mockTicketRepository: ITicketRepository = mockk()
    private val mockRewardRepository: IRewardRepository = mockk()

    private val interactor = LotteryInteractor(mockTicketRepository, mockRewardRepository)

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
    fun shouldNotDeleteRepeatRewardWhenWon() = runTest {
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
        interactor.execute(rewards)

        coVerify(exactly = 0) { mockRewardRepository.delete(any()) }
    }

    @Test
    fun shouldDeleteNonRepeatRewardWhenWon() = runTest {
        coEvery { mockTicketRepository.consumeTicket() } returns Unit
        coEvery { mockRewardRepository.delete(any()) } returns Unit

        val reward = Reward(
            RewardId(1),
            RewardName("reward1"),
            Probability(100F),
            RewardDescription(null),
            false,
        )
        val rewards = RewardCollection(listOf(reward))
        val response = interactor.execute(rewards).getOrNull()!!

        assertThat(response.rewardId).isEqualTo(RewardId(1))
        coVerify(exactly = 1) { mockRewardRepository.delete(reward) }
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
                    false,
                ),
            ),
        )
        val response = interactor.execute(rewards).getOrNull()
        assertThat(response).isNull()
        coVerify(exactly = 0) { mockRewardRepository.delete(any()) }
    }

    @Test
    fun shouldReturnFailureWhenDeleteFails() = runTest {
        coEvery { mockTicketRepository.consumeTicket() } returns Unit
        val deleteError = RuntimeException("delete failed")
        coEvery { mockRewardRepository.delete(any()) } throws deleteError

        val reward = Reward(
            RewardId(1),
            RewardName("reward1"),
            Probability(100F),
            RewardDescription(null),
            false,
        )
        val rewards = RewardCollection(listOf(reward))
        val response = interactor.execute(rewards)

        assertThat(response.isFailure).isTrue()
        assertThat(response.exceptionOrNull()).isSameAs(deleteError)
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
                    false,
                ),
            ),
        )
        val response = interactor.execute(rewards)
        assertThat(response.exceptionOrNull()).isInstanceOf(LackOfTicketsException::class.java)
        coVerify(exactly = 0) { mockRewardRepository.delete(any()) }
    }
}
