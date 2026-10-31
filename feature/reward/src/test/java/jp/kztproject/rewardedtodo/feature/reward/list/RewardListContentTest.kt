package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "en")
class RewardListContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `balance selector follows actual balance while single draw cost stays one`() {
        val balance = mutableIntStateOf(0)
        composeRule.setContent {
            RewardListContent(
                rewards = emptyList(),
                ticket = balance.intValue,
                isSingleLottering = false,
                isBatchLottering = false,
                onSingleLottery = {},
                onBatchLottery = {},
                onRewardItemClick = {},
                onAddNewRewardClick = {},
            )
        }
        composeRule.onNodeWithText("1 ticket").assertExists()
        composeRule.onNodeWithTag("ticket_count").assertTextEquals("0 tickets")
        composeRule.runOnIdle { balance.intValue = 1 }
        composeRule.onNodeWithTag("ticket_count").assertTextEquals("1 ticket")
        composeRule.runOnIdle { balance.intValue = 0 }
        composeRule.onNodeWithTag("ticket_count").assertTextEquals("0 tickets")
        composeRule.onNodeWithText("1 ticket").assertExists()
    }
}
