package jp.kztproject.rewardedtodo.feature.setting

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.application.todo.DeleteApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.GetApiTokenUseCase
import jp.kztproject.rewardedtodo.application.todo.SaveApiTokenUseCase
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class SettingViewModelTest {

    private val mockGetApiTokenUseCase = mockk<GetApiTokenUseCase>()
    private val mockSaveApiTokenUseCase = mockk<SaveApiTokenUseCase>()
    private val mockDeleteApiTokenUseCase = mockk<DeleteApiTokenUseCase>(relaxed = true)

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads access token and updates hasAccessToken state`() = runTest {
        // Given
        val testToken = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        coEvery { mockGetApiTokenUseCase.execute() } returns testToken

        // When
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )

        // Then
        assertThat(viewModel.hasAccessToken.value).isTrue()
        assertThat(viewModel.tokenUiState.value.hasToken).isTrue()
        assertThat(viewModel.tokenUiState.value.isConnected).isTrue()
    }

    @Test
    fun `updateTokenInput updates token input and clears validation error`() = runTest {
        // Given
        coEvery { mockGetApiTokenUseCase.execute() } returns null
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )

        // When
        viewModel.updateTokenInput("new_token")

        // Then
        assertThat(viewModel.tokenUiState.value.tokenInput).isEqualTo("new_token")
        assertThat(viewModel.tokenUiState.value.validationError).isNull()
    }

    @Test
    fun `saveToken with blank input shows TOKEN_EMPTY error`() = runTest {
        // Given
        coEvery { mockGetApiTokenUseCase.execute() } returns null
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )
        viewModel.updateTokenInput("   ")

        // When
        viewModel.saveToken()

        // Then
        assertThat(viewModel.tokenUiState.value.validationError)
            .isEqualTo(TokenValidationError.TOKEN_EMPTY)
        assertThat(viewModel.tokenUiState.value.isLoading).isFalse()
        coVerify(exactly = 0) { mockSaveApiTokenUseCase.execute(any()) }
    }

    @Test
    fun `saveToken with valid token succeeds`() = runTest {
        // Given
        val testToken = "0123456789abcdef0123456789abcdef01234567"
        val apiToken = ApiToken.createSafely(testToken)
        coEvery { mockGetApiTokenUseCase.execute() } returns null andThen apiToken
        coEvery { mockSaveApiTokenUseCase.execute(testToken) } returns Result.success(Unit)
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )
        viewModel.updateTokenInput(testToken)

        // When
        viewModel.saveToken()

        // Then
        assertThat(viewModel.tokenUiState.value.hasToken).isTrue()
        assertThat(viewModel.tokenUiState.value.isConnected).isTrue()
        assertThat(viewModel.tokenUiState.value.tokenInput).isEmpty()
        assertThat(viewModel.tokenUiState.value.validationError).isNull()
        assertThat(viewModel.tokenUiState.value.isLoading).isFalse()
        assertThat(viewModel.hasAccessToken.value).isTrue()
        coVerify { mockSaveApiTokenUseCase.execute(testToken) }
    }

    @Test
    fun `saveToken with InvalidFormat error shows INVALID_TOKEN_FORMAT error`() = runTest {
        // Given
        coEvery { mockGetApiTokenUseCase.execute() } returns null
        coEvery { mockSaveApiTokenUseCase.execute("invalid_token") } returns
            Result.failure(TokenError.InvalidFormat)
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )
        viewModel.updateTokenInput("invalid_token")

        // When
        viewModel.saveToken()

        // Then
        assertThat(viewModel.tokenUiState.value.validationError)
            .isEqualTo(TokenValidationError.INVALID_TOKEN_FORMAT)
        assertThat(viewModel.tokenUiState.value.isLoading).isFalse()
        assertThat(viewModel.tokenUiState.value.hasToken).isFalse()
    }

    @Test
    fun `saveToken with EmptyToken error shows TOKEN_EMPTY error`() = runTest {
        // Given
        coEvery { mockGetApiTokenUseCase.execute() } returns null
        coEvery { mockSaveApiTokenUseCase.execute("token") } returns
            Result.failure(TokenError.EmptyToken)
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )
        viewModel.updateTokenInput("token")

        // When
        viewModel.saveToken()

        // Then
        assertThat(viewModel.tokenUiState.value.validationError)
            .isEqualTo(TokenValidationError.TOKEN_EMPTY)
        assertThat(viewModel.tokenUiState.value.isLoading).isFalse()
    }

    @Test
    fun `saveToken with unknown error shows FAILED_TO_SAVE_TOKEN error`() = runTest {
        // Given
        coEvery { mockGetApiTokenUseCase.execute() } returns null
        coEvery { mockSaveApiTokenUseCase.execute("token") } returns
            Result.failure(Exception("Unknown error"))
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )
        viewModel.updateTokenInput("token")

        // When
        viewModel.saveToken()

        // Then
        assertThat(viewModel.tokenUiState.value.validationError)
            .isEqualTo(TokenValidationError.FAILED_TO_SAVE_TOKEN)
        assertThat(viewModel.tokenUiState.value.isLoading).isFalse()
    }

    @Test
    fun `deleteToken removes token and updates states`() = runTest {
        // Given
        val testToken = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        coEvery { mockGetApiTokenUseCase.execute() } returns testToken andThen null
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )

        // When
        viewModel.deleteToken()

        // Then
        assertThat(viewModel.tokenUiState.value.hasToken).isFalse()
        assertThat(viewModel.tokenUiState.value.isConnected).isFalse()
        assertThat(viewModel.tokenUiState.value.tokenInput).isEmpty()
        assertThat(viewModel.tokenUiState.value.validationError).isNull()
        assertThat(viewModel.tokenUiState.value.isLoading).isFalse()
        assertThat(viewModel.hasAccessToken.value).isFalse()
        coVerify { mockDeleteApiTokenUseCase.execute() }
    }

    @Test
    fun `loadAccessToken updates hasAccessToken when token exists`() = runTest {
        // Given
        val testToken = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        coEvery { mockGetApiTokenUseCase.execute() } returns null andThen testToken
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )

        // When
        viewModel.loadAccessToken()

        // Then
        assertThat(viewModel.hasAccessToken.value).isTrue()
    }

    @Test
    fun `loadAccessToken updates hasAccessToken when token does not exist`() = runTest {
        // Given
        val testToken = ApiToken.createSafely("0123456789abcdef0123456789abcdef01234567")
        coEvery { mockGetApiTokenUseCase.execute() } returns testToken andThen null
        viewModel = SettingViewModel(
            mockGetApiTokenUseCase,
            mockSaveApiTokenUseCase,
            mockDeleteApiTokenUseCase,
        )

        // When
        viewModel.loadAccessToken()

        // Then
        assertThat(viewModel.hasAccessToken.value).isFalse()
    }
}
