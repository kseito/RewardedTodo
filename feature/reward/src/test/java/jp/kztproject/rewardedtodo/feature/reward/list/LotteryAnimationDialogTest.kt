package jp.kztproject.rewardedtodo.feature.reward.list

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-420dpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LotteryAnimationDialogTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `single result stays hidden until opening finishes`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            LotteryAnimationDialog(true, listOf("Coffee"), 0, false, {})
        }
        composeRule.mainClock.advanceTimeBy(1000)
        count("Coffee") shouldBe 0
        capture("capsule")
        composeRule.mainClock.advanceTimeBy(2000)
        count("Coffee") shouldBe 1
        count("Done") shouldBe 1
    }

    @Test
    fun `skip during network wait waits for actual result and does not dismiss`() {
        val ready = mutableStateOf(false)
        var dismissCount = 0
        composeRule.setContent {
            LotteryAnimationDialog(ready.value, if (ready.value) listOf("Coffee") else emptyList(), 0, false) {
                dismissCount++
            }
        }
        composeRule.onNodeWithText("Skip animation").performClick()
        count("Preparing your draw…") shouldBe 1
        count("Coffee") shouldBe 0
        dismissCount shouldBe 0
        composeRule.runOnIdle { ready.value = true }
        count("Coffee") shouldBe 1
        composeRule.onNodeWithText("Done").performClick()
        dismissCount shouldBe 1
    }

    @Test
    fun `batch skip preserves duplicate prizes and total misses`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            LotteryAnimationDialog(true, listOf("Coffee", "Coffee"), 8, true, {})
        }
        composeRule.onNodeWithText("Skip animation").performClick()
        composeRule.mainClock.advanceTimeBy(500)
        count("Coffee") shouldBe 2
        count("Won: 2 · Missed: 8") shouldBe 1
        capture("batch-results")
    }

    @Test
    fun `miss result survives state restoration without replaying opening`() {
        composeRule.mainClock.autoAdvance = false
        val restoration = StateRestorationTester(composeRule)
        restoration.setContent {
            LotteryAnimationDialog(true, emptyList(), 1, false, {})
        }
        composeRule.onNodeWithText("Skip animation").performClick()
        composeRule.mainClock.advanceTimeBy(500)
        count("No reward this time") shouldBe 1
        restoration.emulateSavedInstanceStateRestore()
        composeRule.mainClock.advanceTimeBy(32)
        count("No reward this time") shouldBe 1
        count("What is inside…?") shouldBe 0
    }

    private fun capture(name: String) {
        val bitmap = composeRule.onNode(isDialog()).captureToImage().asAndroidBitmap()
        val output = File("build/lottery-previews/$name.png")
        output.parentFile?.mkdirs()
        output.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun count(text: String): Int = composeRule.onAllNodesWithText(text).fetchSemanticsNodes().size
}
