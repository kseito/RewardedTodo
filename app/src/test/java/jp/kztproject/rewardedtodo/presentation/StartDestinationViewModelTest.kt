package jp.kztproject.rewardedtodo.presentation

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.todo.GetTodoistCredentialUseCase
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class StartDestinationViewModelTest {

    private val getTodoistCredentialUseCase = mockk<GetTodoistCredentialUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `連携済みならホーム画面から始める`() = runTest {
        coEvery { getTodoistCredentialUseCase.execute() } returns
            TodoistCredential(ApiToken.create("0123456789abcdef0123456789abcdef01234567"))

        val viewModel = StartDestinationViewModel(getTodoistCredentialUseCase)

        viewModel.startDestination.value shouldBe StartDestination.HOME
    }

    @Test
    fun `未連携なら認証画面から始める`() = runTest {
        coEvery { getTodoistCredentialUseCase.execute() } returns null

        val viewModel = StartDestinationViewModel(getTodoistCredentialUseCase)

        viewModel.startDestination.value shouldBe StartDestination.AUTH
    }

    @Test
    fun `読み取りに失敗しても判定は完了し認証画面から始める`() = runTest {
        coEvery { getTodoistCredentialUseCase.execute() } throws IllegalStateException()

        val viewModel = StartDestinationViewModel(getTodoistCredentialUseCase)

        viewModel.startDestination.value shouldBe StartDestination.AUTH
    }
}
