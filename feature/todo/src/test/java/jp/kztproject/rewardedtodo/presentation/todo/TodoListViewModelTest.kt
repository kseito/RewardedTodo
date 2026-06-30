package jp.kztproject.rewardedtodo.presentation.todo

import com.google.common.truth.Truth.assertThat
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.reward.CompleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.DeleteTodoUseCase
import jp.kztproject.rewardedtodo.application.reward.FetchTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.GetTodoListUseCase
import jp.kztproject.rewardedtodo.application.reward.UpdateTodoUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
                    every { getApiTokenUseCase.executeAsFlow() } returns flowOf(dummyToken)

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
                    every { getApiTokenUseCase.executeAsFlow() } returns flowOf(dummyToken)

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

        context("When the initial load is in flight") {
            should("not show the pull-to-refresh indicator") {
                runTest(testDispatcher) {
                    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
                    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
                    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
                    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
                    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)
                    val getApiTokenUseCase = mockk<GetApiTokenUseCase>(relaxed = true)
                    val dummyToken = ApiToken.create("1234567890abcdef1234567890abcdef12345678")
                    every { getApiTokenUseCase.executeAsFlow() } returns flowOf(dummyToken)
                    val fetchGate = CompletableDeferred<Unit>()
                    coEvery { fetchTodoListUseCase.execute() } coAnswers { fetchGate.await() }

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
                    // 初回ロードの同期中でもスピナーは出さない
                    assertThat(viewModel.isRefreshing.value).isFalse()

                    fetchGate.complete(Unit)
                    advanceUntilIdle()
                    job.cancel()
                }
            }
        }

        context("When a manual refresh is in flight") {
            should("show the pull-to-refresh indicator") {
                runTest(testDispatcher) {
                    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
                    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
                    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
                    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
                    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)
                    val getApiTokenUseCase = mockk<GetApiTokenUseCase>(relaxed = true)
                    val dummyToken = ApiToken.create("1234567890abcdef1234567890abcdef12345678")
                    every { getApiTokenUseCase.executeAsFlow() } returns flowOf(dummyToken)
                    val refreshGate = CompletableDeferred<Unit>()
                    var fetchCount = 0
                    coEvery { fetchTodoListUseCase.execute() } coAnswers {
                        fetchCount++
                        // 初回は即完了、手動リフレッシュ(2回目)は保留してスピナー表示を観測する
                        if (fetchCount >= 2) refreshGate.await()
                    }

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
                    assertThat(viewModel.isRefreshing.value).isFalse()

                    viewModel.refreshTodoList()
                    advanceUntilIdle()
                    // 手動リフレッシュの同期中はスピナーを表示
                    assertThat(viewModel.isRefreshing.value).isTrue()

                    refreshGate.complete(Unit)
                    advanceUntilIdle()
                    assertThat(viewModel.isRefreshing.value).isFalse()
                    job.cancel()
                }
            }
        }

        context("When the initial load is in flight") {
            should("keep isInitialLoading true until the list is loaded") {
                runTest(testDispatcher) {
                    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
                    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
                    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
                    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
                    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)
                    val getApiTokenUseCase = mockk<GetApiTokenUseCase>(relaxed = true)
                    val dummyToken = ApiToken.create("1234567890abcdef1234567890abcdef12345678")
                    every { getApiTokenUseCase.executeAsFlow() } returns flowOf(dummyToken)
                    every { getTodoListUseCase.execute() } returns flowOf(emptyList())
                    val fetchGate = CompletableDeferred<Unit>()
                    coEvery { fetchTodoListUseCase.execute() } coAnswers { fetchGate.await() }

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
                    // 初回同期が完了し一覧が届くまでは初回ロード中
                    assertThat(viewModel.isInitialLoading.value).isTrue()

                    fetchGate.complete(Unit)
                    advanceUntilIdle()
                    // 一覧が届いたら初回ロード完了
                    assertThat(viewModel.isInitialLoading.value).isFalse()
                    job.cancel()
                }
            }
        }

        context("When there is no auth token") {
            should("clear isInitialLoading after the list is loaded without fetching") {
                runTest(testDispatcher) {
                    val getTodoListUseCase = mockk<GetTodoListUseCase>(relaxed = true)
                    val fetchTodoListUseCase = mockk<FetchTodoListUseCase>(relaxed = true)
                    val updateTodoListUseCase = mockk<UpdateTodoUseCase>(relaxed = true)
                    val deleteTodoUseCase = mockk<DeleteTodoUseCase>(relaxed = true)
                    val completeTodoUseCase = mockk<CompleteTodoUseCase>(relaxed = true)
                    val getApiTokenUseCase = mockk<GetApiTokenUseCase>(relaxed = true)
                    every { getApiTokenUseCase.executeAsFlow() } returns flowOf(null)
                    every { getTodoListUseCase.execute() } returns flowOf(emptyList())

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
                    assertThat(viewModel.isInitialLoading.value).isFalse()
                    coVerify(exactly = 0) { fetchTodoListUseCase.execute() }
                    job.cancel()
                }
            }
        }
    })
