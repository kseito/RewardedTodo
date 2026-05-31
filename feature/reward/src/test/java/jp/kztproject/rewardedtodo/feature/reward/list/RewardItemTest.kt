package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
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

    @Test
    fun `shows repeat icon when needRepeat is true`() {
        composeRule.setContent {
            RewardItem(reward = DummyCreator.createDummyReward(), onRewardItemClick = {})
        }

        composeRule.onNodeWithContentDescription("Repeat").assertExists()
    }

    @Test
    fun `hides repeat icon when needRepeat is false`() {
        composeRule.setContent {
            RewardItem(reward = DummyCreator.createDummyNoRepeatReward(), onRewardItemClick = {})
        }

        composeRule.onAllNodesWithContentDescription("Repeat").assertCountEquals(0)
    }
}
