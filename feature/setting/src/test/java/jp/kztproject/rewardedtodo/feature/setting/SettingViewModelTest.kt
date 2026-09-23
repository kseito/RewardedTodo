package jp.kztproject.rewardedtodo.feature.setting

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class SettingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockDisconnectTodoistUseCase = mockk<DisconnectTodoistUseCase>(relaxed = true)

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingViewModel(mockDisconnectTodoistUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ログアウトを要求すると確認ダイアログが開く`() = runTest {
        viewModel.requestLogout()

        viewModel.uiState.value.isConfirmingLogout shouldBe true
    }

    @Test
    fun `確認ダイアログを閉じてもログアウトは実行されない`() = runTest {
        viewModel.requestLogout()

        viewModel.dismissLogoutConfirmation()

        viewModel.uiState.value.isConfirmingLogout shouldBe false
        coVerify(exactly = 0) { mockDisconnectTodoistUseCase.execute() }
    }

    @Test
    fun `ログアウトすると連携解除が呼ばれ完了が状態に載る`() = runTest {
        coEvery { mockDisconnectTodoistUseCase.execute() } returns Result.success(Unit)
        viewModel.requestLogout()

        viewModel.logout()

        coVerify(exactly = 1) { mockDisconnectTodoistUseCase.execute() }
        viewModel.uiState.value.isLoggedOut shouldBe true
        viewModel.uiState.value.isConfirmingLogout shouldBe false
    }

    @Test
    fun `ログアウト完了を消費すると状態から消える`() = runTest {
        coEvery { mockDisconnectTodoistUseCase.execute() } returns Result.success(Unit)
        viewModel.logout()

        viewModel.consumeLoggedOut()

        viewModel.uiState.value.isLoggedOut shouldBe false
    }

    @Test
    fun `ログアウトに失敗するとエラーが出てローディングが解除される`() = runTest {
        coEvery { mockDisconnectTodoistUseCase.execute() } returns Result.failure(IllegalStateException())

        viewModel.logout()

        viewModel.uiState.value.error shouldBe SettingError.LOGOUT_FAILED
        viewModel.uiState.value.isLoading shouldBe false
    }

    @Test
    fun `エラーを消費するとクリアされる`() = runTest {
        coEvery { mockDisconnectTodoistUseCase.execute() } returns Result.failure(IllegalStateException())
        viewModel.logout()

        viewModel.consumeError()

        viewModel.uiState.value.error shouldBe null
    }
}
