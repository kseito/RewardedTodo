package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "en")
class RewardItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `announces repeat state once through localized text`() {
        composeRule.setContent {
            RewardItem(reward = DummyCreator.createDummyReward(), onRewardItemClick = {})
        }

        composeRule.onNodeWithText("Repeat reward").assertExists()
        composeRule.onAllNodesWithContentDescription("Repeat").assertCountEquals(0)
    }

    @Test
    fun `announces one time state for non repeat reward`() {
        composeRule.setContent {
            RewardItem(reward = DummyCreator.createDummyNoRepeatReward(), onRewardItemClick = {})
        }

        composeRule.onNodeWithText("One time only").assertExists()
        composeRule.onNodeWithText("Repeat reward").assertDoesNotExist()
        composeRule.onAllNodesWithContentDescription("Repeat").assertCountEquals(0)
    }
}
