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
import jp.kztproject.rewardedtodo.domain.todo.EditingTodo
import jp.kztproject.rewardedtodo.domain.todo.Todo
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
                        override fun execute(): Flow<List<Todo>> {
                            return flowOf(
                                listOf(
                                    Todo(1, 1001, "英語学習", 3, true),
                                ),
                            )
                        }
                    },
                    object : FetchTodoListUseCase {
                        override suspend fun execute() {}
                    },
                    object : UpdateTodoUseCase {
                        override suspend fun execute(todo: EditingTodo): Result<Unit> {
                            return Result.success(Unit)
                        }
                    },
                    object : DeleteTodoUseCase {
                        override suspend fun execute(todo: Todo) {}
                    },
                    object : CompleteTodoUseCase {
                        override suspend fun execute(todo: Todo) {}
                    },
                )
            )
        }

        composeRule
            .onRoot()
            .captureRoboImage()
    }
}