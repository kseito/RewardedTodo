package jp.kztproject.rewardedtodo

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.DeleteRewardUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetPointUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.GetRewardsUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.LotteryUseCase
import jp.kztproject.rewardedtodo.application.reward.usecase.SaveRewardUseCase
import jp.kztproject.rewardedtodo.domain.reward.NumberOfTicket
import jp.kztproject.rewardedtodo.domain.reward.Probability
import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardCollection
import jp.kztproject.rewardedtodo.domain.reward.RewardDescription
import jp.kztproject.rewardedtodo.domain.reward.RewardId
import jp.kztproject.rewardedtodo.domain.reward.RewardInput
import jp.kztproject.rewardedtodo.domain.reward.RewardName
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListScreenWithBottomSheet
import jp.kztproject.rewardedtodo.feature.reward.list.RewardListViewModel
import jp.kztproject.rewardedtodo.presentation.todo.TodoListScreenWithBottomSheet
import jp.kztproject.rewardedtodo.presentation.todo.TodoListViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalMaterialApi::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun roborazziTest() {
        composeRule.setContent {
            TodoListScreenWithBottomSheet(
                viewModel = TodoListViewModel(
                    object : GetTodoListUseCase {
                        override fun execute(): Flow<List<Todo>> = flowOf(
                            listOf(
                                Todo(1, 1001, "英語学習", 3, true),
                            ),
                        )
                    },
                    object : FetchTodoListUseCase {
                        override suspend fun execute() {}
                    },
                    object : UpdateTodoUseCase {
                        override suspend fun execute(todo: EditingTodo): Result<Unit> = Result.success(Unit)
                    },
                    object : DeleteTodoUseCase {
                        override suspend fun execute(todo: Todo) {}
                    },
                    object : CompleteTodoUseCase {
                        override suspend fun execute(todo: Todo) {}
                    },
                ),
            )
        }

        composeRule
            .onRoot()
            .captureRoboImage()
    }

    @Test
    fun rewardListScreenTest() {
        // Create mock RewardListViewModel with fake implementations
        val mockViewModel = RewardListViewModel(
            object : LotteryUseCase {
                override suspend fun execute(rewards: RewardCollection): Result<Reward?> = Result.success(null)
            },
            object : GetRewardsUseCase {
                override suspend fun execute(): List<Reward> = listOf(
                    Reward(RewardId(1), RewardName("映画鑑賞"), Probability(80f), RewardDescription("映画を見に行く"), false),
                    Reward(RewardId(2), RewardName("ゲーム"), Probability(50f), RewardDescription("1時間ゲームをする"), true),
                    Reward(RewardId(3), RewardName("お酒"), Probability(30f), RewardDescription("好きなお酒を飲む"), false),
                )

                override suspend fun executeAsFlow(): Flow<List<Reward>> = flowOf(
                    listOf(
                        Reward(RewardId(1), RewardName("映画鑑賞"), Probability(80f), RewardDescription("映画を見に行く"), false),
                        Reward(RewardId(2), RewardName("ゲーム"), Probability(50f), RewardDescription("1時間ゲームをする"), true),
                        Reward(RewardId(3), RewardName("お酒"), Probability(30f), RewardDescription("好きなお酒を飲む"), false),
                    ),
                )
            },
            object : GetPointUseCase {
                override suspend fun execute(): Flow<NumberOfTicket> = flowOf(NumberOfTicket(100))
            },
            object : SaveRewardUseCase {
                override suspend fun execute(reward: RewardInput): Result<Unit> = Result.success(Unit)
            },
            object : DeleteRewardUseCase {
                override suspend fun execute(reward: Reward) {}
            },
        )

        // Set up the test to render RewardListScreenWithBottomSheet
        composeRule.setContent {
            RewardListScreenWithBottomSheet(
                viewModel = mockViewModel,
            )
        }

        // Capture screenshot
        composeRule
            .onRoot()
            .captureRoboImage()
    }
}
