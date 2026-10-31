package jp.kztproject.rewardedtodo.feature.reward.list

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import io.kotest.matchers.shouldBe
import jp.kztproject.rewardedtodo.test.reward.DummyCreator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "en")
class RewardDetailBottomSheetTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `editing preserves reward id and saves precise probability and repeat setting`() {
        val reward = DummyCreator.createDummyNoRepeatReward()
        var saved: List<Any?>? = null
        composeRule.setContent {
            RewardDetailBottomSheetContent(
                reward = reward,
                onRewardSaveSelected = { id, name, description, probability, repeat ->
                    saved = listOf(id, name, description, probability, repeat)
                },
                onRewardDeleteSelected = {},
            )
        }
        composeRule.onNodeWithText("Reward Title").performTextReplacement("Updated reward")
        composeRule.onNodeWithText("Reward Description").performTextReplacement("New description")
        composeRule.onNodeWithText("Chance of Winning(Unit %)").performScrollTo().performTextReplacement("0.25")
        composeRule.onNodeWithContentDescription("repeat_checkbox").performScrollTo().performClick()
        composeRule.onNodeWithText("Save").performScrollTo().performClick()

        saved shouldBe listOf(reward.rewardId.value, "Updated reward", "New description", "0.25", true)
    }

    @Test
    fun `new reward does not offer deletion and starts with repeat disabled`() {
        var savedId: Int? = 99
        var savedRepeat = true
        composeRule.setContent {
            RewardDetailBottomSheetContent(
                reward = null,
                onRewardSaveSelected = { id, _, _, _, repeat ->
                    savedId = id
                    savedRepeat = repeat
                },
                onRewardDeleteSelected = {},
            )
        }
        composeRule.onNodeWithText("Delete").assertDoesNotExist()
        composeRule.onNodeWithText("Save").performScrollTo().performClick()
        savedId shouldBe null
        savedRepeat shouldBe false
    }

    @Test
    fun `delete callback receives the selected reward`() {
        val reward = DummyCreator.createDummyReward()
        var deletedId: Int? = null
        composeRule.setContent {
            RewardDetailBottomSheetContent(
                reward = reward,
                onRewardSaveSelected = { _, _, _, _, _ -> },
                onRewardDeleteSelected = { deletedId = it.rewardId.value },
            )
        }
        composeRule.onNodeWithText("Delete").performScrollTo().performClick()
        deletedId shouldBe reward.rewardId.value
    }
}
