package jp.kztproject.rewardedtodo.feature.setting

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.todo.DeleteApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.SaveApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val mockGetApiTokenUseCase = mockk<GetApiTokenUseCase>()
    private val mockSaveApiTokenUseCase = mockk<SaveApiTokenUseCase>()
    private val mockDeleteApiTokenUseCase = mockk<DeleteApiTokenUseCase>(relaxed = true)

    // トークンソースのリアクティブFlowをMutableStateFlowで模倣する
    private val tokenFlow = MutableStateFlow<ApiToken?>(null)

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockGetApiTokenUseCase.executeAsFlow() } returns tokenFlow
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingViewModel(
        mockGetApiTokenUseCase,
        mockSaveApiTokenUseCase,
        mockDeleteApiTokenUseCase,
    )

    // WhileSubscribedのStateFlowを購読してホット化する
    private fun TestScope.subscribe(vm: SettingViewModel) {
        backgroundScope.launch { vm.tokenUiState.collect {} }
        backgroundScope.launch { vm.hasAccessToken.collect {} }
    }

    @Test
    fun `hasAccessToken and tokenUiState reflect existing token`() = runTest(testDispatcher) {
        // Given
        tokenFlow.value = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        viewModel = createViewModel()

        // When
        subscribe(viewModel)

        // Then
        viewModel.hasAccessToken.value shouldBe true
        viewModel.tokenUiState.value.hasToken shouldBe true
        viewModel.tokenUiState.value.isConnected shouldBe true
    }

    @Test
    fun `updateTokenInput updates token input and clears validation error`() = runTest(testDispatcher) {
        // Given
        viewModel = createViewModel()
        subscribe(viewModel)

        // When
        viewModel.updateTokenInput("new_token")

        // Then
        viewModel.tokenUiState.value.tokenInput shouldBe "new_token"
        viewModel.tokenUiState.value.validationError shouldBe null
    }

    @Test
    fun `saveToken with blank input shows TOKEN_EMPTY error`() = runTest(testDispatcher) {
        // Given
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.updateTokenInput("   ")

        // When
        viewModel.saveToken()

        // Then
        viewModel.tokenUiState.value.validationError shouldBe TokenValidationError.TOKEN_EMPTY
        viewModel.tokenUiState.value.isLoading shouldBe false
        coVerify(exactly = 0) { mockSaveApiTokenUseCase.execute(any()) }
    }

    @Test
    fun `saveToken with valid token succeeds`() = runTest(testDispatcher) {
        // Given
        val testToken = "0123456789abcdef0123456789abcdef01234567"
        coEvery { mockSaveApiTokenUseCase.execute(testToken) } coAnswers {
            tokenFlow.value = ApiToken.create(testToken)
            Result.success(Unit)
        }
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.updateTokenInput(testToken)

        // When
        viewModel.saveToken()

        // Then
        viewModel.tokenUiState.value.hasToken shouldBe true
        viewModel.tokenUiState.value.isConnected shouldBe true
        viewModel.tokenUiState.value.tokenInput.shouldBeEmpty()
        viewModel.tokenUiState.value.validationError shouldBe null
        viewModel.tokenUiState.value.isLoading shouldBe false
        viewModel.hasAccessToken.value shouldBe true
        coVerify { mockSaveApiTokenUseCase.execute(testToken) }
    }

    @Test
    fun `saveToken with InvalidFormat error shows INVALID_TOKEN_FORMAT error`() = runTest(testDispatcher) {
        // Given
        coEvery { mockSaveApiTokenUseCase.execute("invalid_token") } returns
            Result.failure(TokenError.InvalidFormat())
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.updateTokenInput("invalid_token")

        // When
        viewModel.saveToken()

        // Then
        viewModel.tokenUiState.value.validationError shouldBe TokenValidationError.INVALID_TOKEN_FORMAT
        viewModel.tokenUiState.value.isLoading shouldBe false
        viewModel.tokenUiState.value.hasToken shouldBe false
    }

    @Test
    fun `saveToken with EmptyToken error shows TOKEN_EMPTY error`() = runTest(testDispatcher) {
        // Given
        coEvery { mockSaveApiTokenUseCase.execute("token") } returns
            Result.failure(TokenError.EmptyToken())
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.updateTokenInput("token")

        // When
        viewModel.saveToken()

        // Then
        viewModel.tokenUiState.value.validationError shouldBe TokenValidationError.TOKEN_EMPTY
        viewModel.tokenUiState.value.isLoading shouldBe false
    }

    @Test
    fun `saveToken with unknown error shows FAILED_TO_SAVE_TOKEN error`() = runTest(testDispatcher) {
        // Given
        coEvery { mockSaveApiTokenUseCase.execute("token") } returns
            Result.failure(Exception("Unknown error"))
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.updateTokenInput("token")

        // When
        viewModel.saveToken()

        // Then
        viewModel.tokenUiState.value.validationError shouldBe TokenValidationError.FAILED_TO_SAVE_TOKEN
        viewModel.tokenUiState.value.isLoading shouldBe false
    }

    @Test
    fun `deleteToken removes token and updates states`() = runTest(testDispatcher) {
        // Given
        tokenFlow.value = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        coEvery { mockDeleteApiTokenUseCase.execute() } coAnswers { tokenFlow.value = null }
        viewModel = createViewModel()
        subscribe(viewModel)

        // When
        viewModel.deleteToken()

        // Then
        viewModel.tokenUiState.value.hasToken shouldBe false
        viewModel.tokenUiState.value.isConnected shouldBe false
        viewModel.tokenUiState.value.tokenInput.shouldBeEmpty()
        viewModel.tokenUiState.value.validationError shouldBe null
        viewModel.tokenUiState.value.isLoading shouldBe false
        viewModel.hasAccessToken.value shouldBe false
        coVerify { mockDeleteApiTokenUseCase.execute() }
    }

    @Test
    fun `hasAccessToken becomes true when token flow emits a token`() = runTest(testDispatcher) {
        // Given
        tokenFlow.value = null
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.hasAccessToken.value shouldBe false

        // When
        tokenFlow.value = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")

        // Then
        viewModel.hasAccessToken.value shouldBe true
    }

    @Test
    fun `hasAccessToken becomes false when token flow emits null`() = runTest(testDispatcher) {
        // Given
        tokenFlow.value = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        viewModel = createViewModel()
        subscribe(viewModel)
        viewModel.hasAccessToken.value shouldBe true

        // When
        tokenFlow.value = null

        // Then
        viewModel.hasAccessToken.value shouldBe false
    }
}
