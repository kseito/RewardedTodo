package jp.kztproject.rewardedtodo.application.reward.usecase

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.reward.*
import jp.kztproject.rewardedtodo.domain.reward.repository.ILotteryRepository
import jp.kztproject.rewardedtodo.domain.reward.exception.LackOfTicketsException
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BatchLotteryInteractorTest {
    private val repository = mockk<ILotteryRepository>()

    @Test fun `server determines the result and no client lottery is run`() = runTest {
        val winner =
            AcquiredReward(
                AcquiredReward.Id("won"),
                RewardId(1),
                RewardName("Cake"),
                RewardDescription(null),
                "2026-11-07T00:00:00Z",
                null,
            )
        coEvery { repository.draw(10) } returns LotteryOutcome(10, listOf(winner), 9)
        val result = BatchLotteryInteractor(repository).execute(10).getOrThrow()
        result.wonRewards.single() shouldBe winner
        coVerify(exactly = 1) { repository.draw(10) }
    }

    @Test fun `insufficient points returns domain failure`() = runTest {
        coEvery { repository.draw(10) } throws LackOfTicketsException()
        (BatchLotteryInteractor(repository).execute(10).exceptionOrNull() is LackOfTicketsException) shouldBe true
    }
}
