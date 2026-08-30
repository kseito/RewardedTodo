package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RefreshTodoistTokenInteractorTest {

    private val authRepository = mockk<ITodoistAuthRepository>()
    private val credentialRepository = mockk<ITodoistCredentialRepository>(relaxed = true)
    private val now = 1_000_000L
    private val interactor = RefreshTodoistTokenInteractor(authRepository, credentialRepository)

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
    fun `execute refreshes even when the stored token has not expired locally`() = runTest {
        // 401は「ローカルの期限内でもサーバーが拒否した」という意味。ここで期限を見て
        // 早期returnすると同じトークンを返してしまい、Authenticatorの再送が止まる
        val notExpired = TodoistCredential(
            accessToken = ApiToken.create("not-expired-access"),
            refreshToken = refreshToken,
            expiresAt = now + 3_600_000L,
        )
        coEvery { credentialRepository.getCredential() } returns notExpired
        coEvery { authRepository.refreshCredential(refreshToken) } returns Result.success(
            TodoistCredential(ApiToken.create("new-access"), refreshToken, expiresAt = now + 3_600_000L),
        )

        interactor.execute().getOrThrow().value shouldBe "new-access"

        coVerify(exactly = 1) { authRepository.refreshCredential(refreshToken) }
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

    @Test
    fun `execute refreshes only once when concurrent requests hit an expired token`() = runTest {
        // 保存した内容が次のgetCredentialに見えるようにして、実際の順序依存を再現する
        var stored: TodoistCredential? = expiredCredential()
        coEvery { credentialRepository.getCredential() } answers { stored }
        coEvery { credentialRepository.saveCredential(any()) } answers { stored = firstArg() }
        coEvery { authRepository.refreshCredential(refreshToken) } coAnswers {
            // 通信中に後続のリクエストが追いついてくる状況を作る
            delay(1_000L)
            Result.success(
                TodoistCredential(
                    accessToken = ApiToken.create("refreshed-access"),
                    refreshToken = RefreshToken.create("rotated-refresh"),
                    expiresAt = now + 3_600_000L,
                ),
            )
        }

        val results = List(5) { async { interactor.execute() } }.awaitAll()

        // Mutexが無いと5本とも通信し、ローテーションで互いのリフレッシュトークンを無効化する
        coVerify(exactly = 1) { authRepository.refreshCredential(any()) }
        // 待たされた側はロック取得後に再確認し、更新済みのトークンを共有する
        results.forEach { it.getOrThrow().value shouldBe "refreshed-access" }
    }
}
