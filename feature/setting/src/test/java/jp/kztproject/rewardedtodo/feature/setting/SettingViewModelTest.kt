package jp.kztproject.rewardedtodo.feature.setting

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.todo.CompleteTodoistAuthUseCase
import jp.kztproject.rewardedtodo.application.todo.DisconnectTodoistUseCase
import jp.kztproject.rewardedtodo.application.todo.GetTodoistCredentialUseCase
import jp.kztproject.rewardedtodo.application.todo.StartTodoistAuthUseCase
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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

    private val mockGetTodoistCredentialUseCase = mockk<GetTodoistCredentialUseCase>()
    private val mockStartTodoistAuthUseCase = mockk<StartTodoistAuthUseCase>()
    private val mockCompleteTodoistAuthUseCase = mockk<CompleteTodoistAuthUseCase>()
    private val mockDisconnectTodoistUseCase = mockk<DisconnectTodoistUseCase>(relaxed = true)

    // クレデンシャルのリアクティブFlowをMutableStateFlowで模倣する
    private val credentialFlow = MutableStateFlow<TodoistCredential?>(null)

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockGetTodoistCredentialUseCase.executeAsFlow() } returns credentialFlow
        viewModel = SettingViewModel(
            mockGetTodoistCredentialUseCase,
            mockStartTodoistAuthUseCase,
            mockCompleteTodoistAuthUseCase,
            mockDisconnectTodoistUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState reflects the connection state derived from the credential flow`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }

        viewModel.uiState.value.isConnected shouldBe false

        credentialFlow.value = TodoistCredential(ApiToken.create("access-token"))
        viewModel.uiState.value.isConnected shouldBe true

        collector.cancel()
    }

    @Test
    fun `connect emits the authorize url produced by the use case`() = runTest(testDispatcher) {
        val authorizeUrl = "https://todoist.com/oauth/authorize?client_id=x&state=y"
        coEvery { mockStartTodoistAuthUseCase.execute() } returns Result.success(authorizeUrl)

        val emitted = async { viewModel.authorizeRequests.first() }
        viewModel.connect()

        emitted.await() shouldBe authorizeUrl
    }

    @Test
    fun `connect surfaces an error when the authorize url cannot be built`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }
        coEvery { mockStartTodoistAuthUseCase.execute() } returns Result.failure(IllegalStateException("disk full"))

        viewModel.connect()

        viewModel.uiState.value.error shouldBe TodoistAuthError.UNKNOWN
        viewModel.uiState.value.isLoading shouldBe false

        collector.cancel()
    }

    @Test
    fun `onAuthTabResult completes the authorization on success`() = runTest(testDispatcher) {
        val redirectUri = "https://example.com/oauth/callback?code=abc&state=xyz"
        coEvery { mockCompleteTodoistAuthUseCase.execute(redirectUri) } returns Result.success(Unit)

        viewModel.onAuthTabResult(TodoistAuthTabResult.Succeeded(redirectUri))

        coVerify { mockCompleteTodoistAuthUseCase.execute(redirectUri) }
    }

    @Test
    fun `onAuthTabResult maps a state mismatch to a dedicated error`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }
        val redirectUri = "https://example.com/oauth/callback?code=abc&state=forged"
        coEvery { mockCompleteTodoistAuthUseCase.execute(redirectUri) } returns
            Result.failure(TokenError.StateMismatch())

        viewModel.onAuthTabResult(TodoistAuthTabResult.Succeeded(redirectUri))

        viewModel.uiState.value.error shouldBe TodoistAuthError.STATE_MISMATCH
        viewModel.uiState.value.isLoading shouldBe false

        collector.cancel()
    }

    @Test
    fun `onAuthTabResult reports a cancellation`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }

        viewModel.onAuthTabResult(TodoistAuthTabResult.Canceled)

        viewModel.uiState.value.error shouldBe TodoistAuthError.CANCELED
        viewModel.uiState.value.isLoading shouldBe false

        collector.cancel()
    }

    @Test
    fun `onAuthTabResult reports a failed asset link verification`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }

        viewModel.onAuthTabResult(TodoistAuthTabResult.VerificationFailed)

        viewModel.uiState.value.error shouldBe TodoistAuthError.VERIFICATION_FAILED

        collector.cancel()
    }

    @Test
    fun `onAuthTabUnsupported reports a dedicated error without starting the flow`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }

        viewModel.onAuthTabUnsupported()

        viewModel.uiState.value.error shouldBe TodoistAuthError.AUTH_TAB_UNSUPPORTED
        coVerify(exactly = 0) { mockStartTodoistAuthUseCase.execute() }

        collector.cancel()
    }

    @Test
    fun `disconnect clears the credential and resets the edit state`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }
        credentialFlow.value = TodoistCredential(ApiToken.create("access-token"))
        coEvery { mockDisconnectTodoistUseCase.execute() } returns Result.success(Unit)

        viewModel.disconnect()
        credentialFlow.value = null

        viewModel.uiState.value.isConnected shouldBe false
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.error shouldBe null

        collector.cancel()
    }

    @Test
    fun `disconnect stops loading even when it fails so the screen does not freeze`() = runTest(testDispatcher) {
        val collector = TestScope(testDispatcher).launch { viewModel.uiState.collect {} }
        coEvery { mockDisconnectTodoistUseCase.execute() } returns Result.failure(IllegalStateException("failed"))

        viewModel.disconnect()

        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.error shouldBe TodoistAuthError.UNKNOWN

        collector.cancel()
    }
}
