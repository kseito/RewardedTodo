package jp.kztproject.rewardedtodo

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.airbnb.android.showkase.models.Showkase
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalMaterialApi::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [35])
class ShowkaseParameterizedTest(
    private val testCase: TestCase
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureShowkaseComponent() {
        val component = testCase.showkaseBrowserComponent
        println(component)

        composeRule.setContent {
            component.component()
        }

        composeRule
            .onRoot()
            .captureRoboImage(
                filePath = "showkase_parameterized/${component.componentName}_${component.group}.png".replace(" ", "_")
            )
    }

    companion object {
        class TestCase(
            val showkaseBrowserComponent: ShowkaseBrowserComponent
        ) {
            override fun toString() = showkaseBrowserComponent.componentKey
        }


        @ParameterizedRobolectricTestRunner.Parameters(name = "[{index}] {0}")
        @JvmStatic
        fun components(): Iterable<Array<*>> = Showkase.getMetadata().componentList.map {
            arrayOf(TestCase(it))
        }
    }
}