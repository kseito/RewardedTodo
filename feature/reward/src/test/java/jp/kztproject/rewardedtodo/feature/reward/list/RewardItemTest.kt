package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RewardItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun reward(needRepeat: Boolean) = Reward(
        rewardId = RewardId(1),
        name = RewardName("PS5"),
        probability = Probability(1f),
        description = RewardDescription("desc"),
        needRepeat = needRepeat,
    )

    @Test
    fun `shows repeat icon when needRepeat is true`() {
        composeRule.setContent {
            RewardItem(reward = reward(needRepeat = true), onRewardItemClick = {})
        }

        composeRule.onNodeWithContentDescription("Repeat").assertExists()
    }

    @Test
    fun `hides repeat icon when needRepeat is false`() {
        composeRule.setContent {
            RewardItem(reward = reward(needRepeat = false), onRewardItemClick = {})
        }

        composeRule.onAllNodesWithContentDescription("Repeat").assertCountEquals(0)
    }
}
