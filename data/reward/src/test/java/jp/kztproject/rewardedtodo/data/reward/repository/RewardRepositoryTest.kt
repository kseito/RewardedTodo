package jp.kztproject.rewardedtodo.data.reward.repository

import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.*
import jp.kztproject.rewardedtodo.common.rewardserver.RewardUserIdRepository
import jp.kztproject.rewardedtodo.data.reward.network.*
import jp.kztproject.rewardedtodo.domain.reward.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RewardRepositoryTest {
    private val api = mockk<RewardApi>()
    private val users = mockk<RewardUserIdRepository> { coEvery { getUserId() } returns "user" }
    private val target = RewardRepository(api, users)

    @Test fun `save uses exact basis points and edit version`() = runTest {
        coEvery { api.update(any(), any(), any()) } returns RewardResponse(1, "Cake", null, 1234, true, 3)
        target.createOrUpdate(RewardInput(1, "Cake", 12.34f, null, true, 2))
        coVerify { api.update("user", 1, SaveRewardRequest("Cake", null, 1234, true, 2)) }
    }

    @Test fun `precision beyond two decimals is rejected rather than rounded`() = runTest {
        shouldThrow<ArithmeticException> { target.createOrUpdate(RewardInput(name = "Cake", probability = 1.234f)) }
        coVerify(exactly = 0) { api.create(any(), any()) }
    }

    @Test fun `list and delete use server ids and versions`() = runTest {
        coEvery { api.list("user") } returns listOf(RewardResponse(42, "Cake", null, 250, false, 3))
        coEvery { api.delete(any(), any(), any()) } just Runs
        val reward = target.findAll().single()
        reward.probability.value shouldBe 2.5f
        target.delete(reward)
        coVerify { api.delete("user", 42, 3) }
    }
}
