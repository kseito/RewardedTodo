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

class LotteryInteractorTest {
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
        coEvery { repository.draw(1) } returns LotteryOutcome(1, listOf(winner), 0)
        val result = LotteryInteractor(repository).execute().getOrThrow()
        result shouldBe winner
        coVerify(exactly = 1) { repository.draw(1) }
    }

    @Test fun `insufficient points returns domain failure`() = runTest {
        coEvery { repository.draw(1) } throws LackOfTicketsException()
        (LotteryInteractor(repository).execute().exceptionOrNull() is LackOfTicketsException) shouldBe true
    }
}
