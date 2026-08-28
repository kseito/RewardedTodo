package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.CurrentTimeProvider
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetValidAccessTokenInteractorTest {

    private val credentialRepository = mockk<ITodoistCredentialRepository>()
    private val refreshTodoistTokenUseCase = mockk<RefreshTodoistTokenUseCase>()
    private val now = 1_000_000L
    private val interactor = GetValidAccessTokenInteractor(
        credentialRepository,
        refreshTodoistTokenUseCase,
        CurrentTimeProvider { now },
    )

    @Test
    fun `execute returns null when not connected`() = runTest {
        coEvery { credentialRepository.getCredential() } returns null

        interactor.execute() shouldBe null

        coVerify(exactly = 0) { refreshTodoistTokenUseCase.execute() }
    }

    @Test
    fun `execute returns the stored token without refreshing when it is still valid`() = runTest {
        coEvery { credentialRepository.getCredential() } returns TodoistCredential(
            accessToken = ApiToken.create("valid-access"),
            refreshToken = RefreshToken.create("refresh"),
            expiresAt = now + 3_600_000L,
        )

        interactor.execute()?.value shouldBe "valid-access"

        coVerify(exactly = 0) { refreshTodoistTokenUseCase.execute() }
    }

    @Test
    fun `execute refreshes an expired token before returning it`() = runTest {
        coEvery { credentialRepository.getCredential() } returns TodoistCredential(
            accessToken = ApiToken.create("expired-access"),
            refreshToken = RefreshToken.create("refresh"),
            expiresAt = now,
        )
        coEvery { refreshTodoistTokenUseCase.execute() } returns Result.success(ApiToken.create("refreshed-access"))

        interactor.execute()?.value shouldBe "refreshed-access"
    }

    @Test
    fun `execute returns null when the refresh fails`() = runTest {
        coEvery { credentialRepository.getCredential() } returns TodoistCredential(
            accessToken = ApiToken.create("expired-access"),
            refreshToken = RefreshToken.create("refresh"),
            expiresAt = now,
        )
        coEvery { refreshTodoistTokenUseCase.execute() } returns
            Result.failure(TokenError.RefreshFailed(IllegalStateException("invalid_grant")))

        interactor.execute() shouldBe null
    }
}
