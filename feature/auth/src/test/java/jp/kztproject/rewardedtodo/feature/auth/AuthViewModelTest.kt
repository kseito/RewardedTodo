package jp.kztproject.rewardedtodo.feature.auth

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthUseCase
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthUseCase
import jp.kztproject.rewardedtodo.domain.todo.TokenError
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
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockStartTodoistAuthUseCase = mockk<StartTodoistAuthUseCase>()
    private val mockCompleteTodoistAuthUseCase = mockk<CompleteTodoistAuthUseCase>()

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(mockStartTodoistAuthUseCase, mockCompleteTodoistAuthUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `連携を開始すると認可URLが状態に載る`() = runTest {
        val authorizeUrl = "https://todoist.com/oauth/authorize?state=s"
        coEvery { mockStartTodoistAuthUseCase.execute() } returns Result.success(authorizeUrl)

        viewModel.connect()

        viewModel.uiState.value.authorizeUrl shouldBe authorizeUrl
    }

    @Test
    fun `認可URLを消費すると状態から消える`() = runTest {
        coEvery { mockStartTodoistAuthUseCase.execute() } returns
            Result.success("https://todoist.com/oauth/authorize?state=s")
        viewModel.connect()

        viewModel.consumeAuthorizeUrl()

        viewModel.uiState.value.authorizeUrl shouldBe null
    }

    @Test
    fun `認可URLの発行に失敗するとエラーが出る`() = runTest {
        coEvery { mockStartTodoistAuthUseCase.execute() } returns Result.failure(IllegalStateException())

        viewModel.connect()

        viewModel.uiState.value.error shouldBe AuthError.UNKNOWN
        viewModel.uiState.value.isLoading shouldBe false
    }

    @Test
    fun `Auth Tab非対応ならその旨のエラーが出る`() = runTest {
        viewModel.onAuthTabUnsupported()

        viewModel.uiState.value.error shouldBe AuthError.AUTH_TAB_UNSUPPORTED
    }

    @Test
    fun `認可が成功すると連携完了が状態に載る`() = runTest {
        coEvery { mockCompleteTodoistAuthUseCase.execute(any()) } returns Result.success(Unit)

        viewModel.onAuthTabResult(TodoistAuthTabResult.Succeeded("https://example.com/callback?code=c&state=s"))

        coVerify(exactly = 1) { mockCompleteTodoistAuthUseCase.execute(any()) }
        viewModel.uiState.value.isAuthenticated shouldBe true
        viewModel.uiState.value.error shouldBe null
    }

    @Test
    fun `連携完了を消費すると状態から消える`() = runTest {
        coEvery { mockCompleteTodoistAuthUseCase.execute(any()) } returns Result.success(Unit)
        viewModel.onAuthTabResult(TodoistAuthTabResult.Succeeded("https://example.com/callback"))

        viewModel.consumeAuthenticated()

        viewModel.uiState.value.isAuthenticated shouldBe false
    }

    @Test
    fun `ユーザーがAuth Tabを閉じるとキャンセル扱いになる`() = runTest {
        viewModel.onAuthTabResult(TodoistAuthTabResult.Canceled)

        viewModel.uiState.value.error shouldBe AuthError.CANCELED
    }

    @Test
    fun `リダイレクトの検証に失敗するとその旨のエラーが出る`() = runTest {
        viewModel.onAuthTabResult(TodoistAuthTabResult.VerificationFailed)

        viewModel.uiState.value.error shouldBe AuthError.VERIFICATION_FAILED
    }

    @Test
    fun `トークン交換の失敗はエラー種別ごとに変換される`() = runTest {
        val expected = mapOf(
            TokenError.StateMismatch() to AuthError.STATE_MISMATCH,
            TokenError.AuthorizationCanceled() to AuthError.CANCELED,
            TokenError.AuthorizationFailed("denied") to AuthError.AUTHORIZATION_FAILED,
            TokenError.ExchangeFailed(IllegalStateException()) to AuthError.EXCHANGE_FAILED,
            TokenError.InvalidFormat() to AuthError.UNKNOWN,
        )

        expected.forEach { (cause, authError) ->
            coEvery { mockCompleteTodoistAuthUseCase.execute(any()) } returns Result.failure(cause)

            viewModel.onAuthTabResult(TodoistAuthTabResult.Succeeded("https://example.com/callback"))

            viewModel.uiState.value.error shouldBe authError
        }
    }

    @Test
    fun `エラーを消費するとクリアされる`() = runTest {
        viewModel.onAuthTabResult(TodoistAuthTabResult.Canceled)

        viewModel.consumeError()

        viewModel.uiState.value.error shouldBe null
    }
}
