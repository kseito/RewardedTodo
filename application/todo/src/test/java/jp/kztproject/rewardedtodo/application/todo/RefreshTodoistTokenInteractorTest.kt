package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.CurrentTimeProvider
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RefreshTodoistTokenInteractorTest {

    private val authRepository = mockk<ITodoistAuthRepository>()
    private val credentialRepository = mockk<ITodoistCredentialRepository>(relaxed = true)
    private var now = 1_000_000L
    private val interactor = RefreshTodoistTokenInteractor(
        authRepository,
        credentialRepository,
        CurrentTimeProvider { now },
    )

    private val refreshToken = RefreshToken.create("refresh-token")

    private fun expiredCredential() = TodoistCredential(
        accessToken = ApiToken.create("old-access"),
        refreshToken = refreshToken,
        expiresAt = now,
    )

    @Test
    fun `execute refreshes an expired credential and saves the rotated refresh token`() = runTest {
        val rotated = RefreshToken.create("rotated-refresh")
        coEvery { credentialRepository.getCredential() } returns expiredCredential()
        coEvery { authRepository.refreshCredential(refreshToken) } returns Result.success(
            TodoistCredential(ApiToken.create("new-access"), rotated, expiresAt = now + 3_600_000L),
        )
        val saved = slot<TodoistCredential>()
        coEvery { credentialRepository.saveCredential(capture(saved)) } returns Unit

        val result = interactor.execute()

        result.getOrThrow().value shouldBe "new-access"
        saved.captured.refreshToken shouldBe rotated
    }

    @Test
    fun `execute keeps the previous refresh token when the response omits it`() = runTest {
        // 消費済みリフレッシュトークンの60秒以内の再試行ではrefresh_tokenが返らない。
        // ここで値を失うと以降リフレッシュ不能になり再連携が必要になる
        coEvery { credentialRepository.getCredential() } returns expiredCredential()
        coEvery { authRepository.refreshCredential(refreshToken) } returns Result.success(
            TodoistCredential(ApiToken.create("new-access"), refreshToken = null, expiresAt = now + 3_600_000L),
        )
        val saved = slot<TodoistCredential>()
        coEvery { credentialRepository.saveCredential(capture(saved)) } returns Unit

        interactor.execute().getOrThrow()

        saved.captured.refreshToken shouldBe refreshToken
    }

    @Test
    fun `execute skips the network call when the credential is still valid`() = runTest {
        // ロック待ちの間に別のリクエストがリフレッシュを終えていた場合に二重更新しない
        coEvery { credentialRepository.getCredential() } returns TodoistCredential(
            accessToken = ApiToken.create("still-valid"),
            refreshToken = refreshToken,
            expiresAt = now + 3_600_000L,
        )

        interactor.execute().getOrThrow().value shouldBe "still-valid"

        coVerify(exactly = 0) { authRepository.refreshCredential(any()) }
    }

    @Test
    fun `execute fails when not connected`() = runTest {
        coEvery { credentialRepository.getCredential() } returns null

        interactor.execute().exceptionOrNull().shouldBeInstanceOf<TokenError.NotConnected>()
    }

    @Test
    fun `execute fails when there is no refresh token to use`() = runTest {
        coEvery { credentialRepository.getCredential() } returns TodoistCredential(
            accessToken = ApiToken.create("old-access"),
            refreshToken = null,
            expiresAt = now,
        )

        interactor.execute().exceptionOrNull().shouldBeInstanceOf<TokenError.RefreshFailed>()
    }

    @Test
    fun `execute wraps a refresh failure and leaves the stored credential untouched`() = runTest {
        coEvery { credentialRepository.getCredential() } returns expiredCredential()
        coEvery { authRepository.refreshCredential(refreshToken) } returns
            Result.failure(IllegalStateException("invalid_grant"))

        interactor.execute().exceptionOrNull().shouldBeInstanceOf<TokenError.RefreshFailed>()

        coVerify(exactly = 0) { credentialRepository.saveCredential(any()) }
    }
}
