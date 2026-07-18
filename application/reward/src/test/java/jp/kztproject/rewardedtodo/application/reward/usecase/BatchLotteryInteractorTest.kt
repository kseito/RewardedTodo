package jp.kztproject.rewardedtodo.application.reward.usecase

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.reward.repository.ITicketRepository
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import jp.kztproject.rewardedtodo.domain.reward.repository.IRewardRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BatchLotteryInteractorTest {

    private val mockTicketRepository: ITicketRepository = mockk()
    private val mockRewardRepository: IRewardRepository = mockk()

    private val interactor = BatchLotteryInteractor(mockTicketRepository, mockRewardRepository)

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

        result.isSuccess shouldBe true
        val batchResult = result.getOrNull()!!
        batchResult.wonRewards shouldHaveSize 3
        batchResult.missCount shouldBe 0
        coVerify(exactly = 1) { mockTicketRepository.consumeTickets(3) }
    }

    @Test
    fun `does not delete repeat reward even when won multiple times`() = runTest {
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
        interactor.execute(rewards, 3)

        coVerify(exactly = 0) { mockRewardRepository.delete(any()) }
    }

    @Test
    fun `deletes non-repeat reward only once even when won multiple times`() = runTest {
        coEvery { mockTicketRepository.consumeTickets(any()) } returns Unit
        coEvery { mockRewardRepository.delete(any()) } returns Unit

        val reward = Reward(
            RewardId(1),
            RewardName("reward1"),
            Probability(100F),
            RewardDescription(null),
            false,
        )
        val rewards = RewardCollection(listOf(reward))
        val result = interactor.execute(rewards, 3)

        result.isSuccess shouldBe true
        result.getOrNull()!!.wonRewards shouldHaveSize 3
        coVerify(exactly = 1) { mockRewardRepository.delete(reward) }
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
                    false,
                ),
            ),
        )
        val result = interactor.execute(rewards, 3)

        result.isSuccess shouldBe true
        val batchResult = result.getOrNull()!!
        batchResult.wonRewards.shouldBeEmpty()
        batchResult.missCount shouldBe 3
        coVerify(exactly = 0) { mockRewardRepository.delete(any()) }
    }

    @Test
    fun `returns failure when delete fails for non-repeat reward`() = runTest {
        coEvery { mockTicketRepository.consumeTickets(any()) } returns Unit
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
        val result = interactor.execute(rewards, 3)

        result.isFailure shouldBe true
        result.exceptionOrNull() shouldBeSameInstanceAs deleteError
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
                    false,
                ),
            ),
        )
        val result = interactor.execute(rewards, 5)

        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<LackOfTicketsException>()
        coVerify(exactly = 0) { mockRewardRepository.delete(any()) }
    }
}
