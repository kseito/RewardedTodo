package jp.kztproject.rewardedtodo.feature.reward.inventory

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.reward.usecase.GetAcquiredRewardsUseCase
import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import jp.kztproject.rewardedtodo.domain.reward.AcquiredRewardPage
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "en")
class AcquiredRewardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `resuming the same screen removes a reward used on another device`() {
        val reward = AcquiredReward(
            AcquiredReward.Id("reward"),
            RewardId(1),
            RewardName("Coffee"),
            RewardDescription(null),
            "2026-11-07T00:00:00Z",
            null,
        )
        val getRewards = mockk<GetAcquiredRewardsUseCase>()
        coEvery { getRewards.execute(false, null) } returnsMany listOf(
            AcquiredRewardPage(listOf(reward), null),
            AcquiredRewardPage(emptyList(), null),
        )
        val viewModel = AcquiredRewardViewModel(getRewards, mockk())
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry(this)
        }

        composeRule.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.RESUMED }
        composeRule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                AcquiredRewardScreen(viewModel)
            }
        }
        composeRule.onNodeWithText("Coffee").assertExists()
        coVerify(exactly = 1) { getRewards.execute(false, null) }

        composeRule.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.CREATED }
        composeRule.runOnIdle { owner.lifecycle.currentState = Lifecycle.State.RESUMED }

        composeRule.onNodeWithText("Coffee").assertDoesNotExist()
        composeRule.onNodeWithText("No rewards to display").assertExists()
        coVerify(exactly = 2) { getRewards.execute(false, null) }
    }
}
