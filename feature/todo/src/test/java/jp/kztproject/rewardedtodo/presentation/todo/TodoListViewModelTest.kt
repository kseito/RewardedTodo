package jp.kztproject.rewardedtodo.presentation.todo

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListViewModelTest : ShouldSpec({

    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)

    lateinit var viewModel: TodoListViewModel

    beforeSpec {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)

        viewModel = TodoListViewModel(
            getTodoListUseCase,
            fetchTodoListUseCase,
            updateTodoListUseCase,
            deleteTodoUseCase,
            completeTodoUseCase
        )
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    context("When viewModel is initialized") {
        should("get todo list") {
            coVerify(exactly = 1) { fetchTodoListUseCase.execute() }
        }
    }

    context("When refreshTodoList is called") {
        should("get todo list if successful") {
            viewModel.refreshTodoList()

            coVerify(exactly = 2) { fetchTodoListUseCase.execute() }
        }
    }
})