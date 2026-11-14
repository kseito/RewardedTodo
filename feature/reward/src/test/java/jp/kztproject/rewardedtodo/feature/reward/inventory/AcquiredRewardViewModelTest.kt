package jp.kztproject.rewardedtodo.feature.reward.inventory

import io.kotest.matchers.shouldBe
import io.mockk.*
import jp.kztproject.rewardedtodo.application.reward.usecase.*
import jp.kztproject.rewardedtodo.domain.reward.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AcquiredRewardViewModelTest {
    @After fun reset() {
        Dispatchers.resetMain()
    }
    private fun reward(id: String) = AcquiredReward(
        AcquiredReward.Id(id),
        RewardId(1),
        RewardName("Cake"),
        RewardDescription(null),
        "2026-11-07T00:00:00Z",
        null,
    )

    @Test fun `failed use retains inventory and successful use removes only that acquisition`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val get = mockk<GetAcquiredRewardsUseCase>()
        val use = mockk<UseAcquiredRewardUseCase>()
        val first = reward("first")
        val second = reward("second")
        coEvery { get.execute(false, null) } returns AcquiredRewardPage(listOf(first, second), null)
        coEvery { use.execute(first.id) } throws IOException()
        val vm = AcquiredRewardViewModel(get, use)
        advanceUntilIdle()
        vm.use(first.id)
        advanceUntilIdle()
        vm.state.value.items shouldBe listOf(first, second)
        (vm.state.value.error is IOException) shouldBe true
        coEvery { use.execute(first.id) } returns first.copy(usedAt = "2026-11-07T01:00:00Z")
        vm.use(first.id)
        advanceUntilIdle()
        vm.state.value.items shouldBe listOf(second)
    }

    @Test fun `failed next page preserves items and retry uses same cursor`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val get = mockk<GetAcquiredRewardsUseCase>()
        val use = mockk<UseAcquiredRewardUseCase>()
        coEvery { get.execute(false, null) } returns AcquiredRewardPage(listOf(reward("first")), "next")
        coEvery { get.execute(false, "next") } throws IOException()
        val vm = AcquiredRewardViewModel(get, use)
        advanceUntilIdle()
        vm.loadMore()
        advanceUntilIdle()
        vm.state.value.items.size shouldBe 1
        vm.state.value.cursor shouldBe "next"
        coEvery { get.execute(false, "next") } returns AcquiredRewardPage(listOf(reward("second")), null)
        vm.loadMore()
        advanceUntilIdle()
        vm.state.value.items.map { it.id.value } shouldBe listOf("first", "second")
        vm.state.value.cursor shouldBe null
    }
}
