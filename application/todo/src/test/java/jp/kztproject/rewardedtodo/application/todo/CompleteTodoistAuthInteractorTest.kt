package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.AuthorizationCode
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.OAuthState
import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CompleteTodoistAuthInteractorTest {

    private val authRepository = mockk<ITodoistAuthRepository>()
    private val authSessionStore = TodoistAuthSessionStore()
    private val credentialRepository = mockk<ITodoistCredentialRepository>(relaxed = true)
    private val interactor =
        CompleteTodoistAuthInteractor(authRepository, authSessionStore, credentialRepository)

    private val codeVerifier = CodeVerifier.generate()
    private val session = TodoistAuthSession(state = OAuthState.create("issued-state"), codeVerifier = codeVerifier)
    private val credential = TodoistCredential(ApiToken.create("access-token"))

    @Test
    fun `execute exchanges the code and saves the credential`() = runTest {
        authSessionStore.save(session)
        coEvery { authRepository.exchangeCodeForCredential(any(), any()) } returns Result.success(credential)

        val result = interactor.execute("https://example.com/oauth/callback?code=auth-code&state=issued-state")

        result.isSuccess shouldBe true
        coVerify {
            authRepository.exchangeCodeForCredential(AuthorizationCode.create("auth-code"), codeVerifier)
        }
        coVerify { credentialRepository.saveCredential(credential) }
        // 認可コードもverifierも使い切りのため破棄する
        authSessionStore.get() shouldBe null
    }

    @Test
    fun `execute rejects a redirect whose state does not match the issued one`() = runTest {
        authSessionStore.save(session)

        val result = interactor.execute("https://example.com/oauth/callback?code=auth-code&state=forged-state")

        result.exceptionOrNull().shouldBeInstanceOf<TokenError.StateMismatch>()
        // 第三者が差し込んだ認可コードを交換してはいけない
        coVerify(exactly = 0) { authRepository.exchangeCodeForCredential(any(), any()) }
    }

    @Test
    fun `execute rejects a redirect when no session is pending`() = runTest {
        val result = interactor.execute("https://example.com/oauth/callback?code=auth-code&state=issued-state")

        result.exceptionOrNull().shouldBeInstanceOf<TokenError.StateMismatch>()
        coVerify(exactly = 0) { authRepository.exchangeCodeForCredential(any(), any()) }
    }

    @Test
    fun `execute reports the error returned by the authorization screen`() = runTest {
        authSessionStore.save(session)

        val result = interactor.execute("https://example.com/oauth/callback?error=access_denied")

        val error = result.exceptionOrNull().shouldBeInstanceOf<TokenError.AuthorizationFailed>()
        error.reason shouldBe "access_denied"
        coVerify(exactly = 0) { authRepository.exchangeCodeForCredential(any(), any()) }
    }

    @Test
    fun `execute fails when the redirect carries no authorization code`() = runTest {
        authSessionStore.save(session)

        val result = interactor.execute("https://example.com/oauth/callback?state=issued-state")

        result.exceptionOrNull().shouldBeInstanceOf<TokenError.AuthorizationFailed>()
    }

    @Test
    fun `execute wraps a token exchange failure and clears the session`() = runTest {
        authSessionStore.save(session)
        coEvery { authRepository.exchangeCodeForCredential(any(), any()) } returns
            Result.failure(IllegalStateException("bad_authorization_code"))

        val result = interactor.execute("https://example.com/oauth/callback?code=auth-code&state=issued-state")

        result.exceptionOrNull().shouldBeInstanceOf<TokenError.ExchangeFailed>()
        coVerify(exactly = 0) { credentialRepository.saveCredential(any()) }
        authSessionStore.get() shouldBe null
    }
}
