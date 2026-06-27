package jp.kztproject.rewardedtodo.presentation.todo

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListViewModelTest :
    ShouldSpec({

        val testDispatcher = StandardTestDispatcher()

        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        context("When todoList is subscribed") {
            should("fetch todo list once") {
                runTest(testDispatcher) {
                    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
                    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
                    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
                    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
                    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)
                    val getApiTokenUseCase = mockk<GetApiTokenUseCase>(relaxed = true)
                    val dummyToken = ApiToken.create("1234567890abcdef1234567890abcdef12345678")
                    coEvery { getApiTokenUseCase.execute() } returns dummyToken

                    val viewModel = TodoListViewModel(
                        getTodoListUseCase,
                        fetchTodoListUseCase,
                        updateTodoListUseCase,
                        deleteTodoUseCase,
                        completeTodoUseCase,
                        getApiTokenUseCase,
                    )

                    val job = backgroundScope.launch { viewModel.todoList.collect {} }
                    advanceUntilIdle()
                    coVerify(exactly = 1) { fetchTodoListUseCase.execute() }
                    job.cancel()
                }
            }
        }

        context("When refreshTodoList is called") {
            should("call fetchTodoListUseCase again") {
                runTest(testDispatcher) {
                    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
                    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
                    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
                    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
                    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)
                    val getApiTokenUseCase = mockk<GetApiTokenUseCase>(relaxed = true)
                    val dummyToken = ApiToken.create("1234567890abcdef1234567890abcdef12345678")
                    coEvery { getApiTokenUseCase.execute() } returns dummyToken

                    val viewModel = TodoListViewModel(
                        getTodoListUseCase,
                        fetchTodoListUseCase,
                        updateTodoListUseCase,
                        deleteTodoUseCase,
                        completeTodoUseCase,
                        getApiTokenUseCase,
                    )

                    val job = backgroundScope.launch { viewModel.todoList.collect {} }
                    advanceUntilIdle()
                    viewModel.refreshTodoList()
                    advanceUntilIdle()

                    coVerify(exactly = 2) { fetchTodoListUseCase.execute() }
                    job.cancel()
                }
            }
        }
    })
