package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession
import jp.kztproject.rewardedtodo.domain.todo.TodoistOAuthConfig
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthSessionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.net.URI
import java.net.URLDecoder

class StartTodoistAuthInteractorTest {

    private val config = TodoistOAuthConfig(
        clientId = "https://example.com/oauth/client.json",
        authorizeUrl = "https://todoist.com/oauth/authorize",
        redirectUri = "https://example.com/oauth/callback",
        scope = "data:read_write",
    )
    private val authSessionRepository = mockk<ITodoistAuthSessionRepository>(relaxed = true)
    private val interactor = StartTodoistAuthInteractor(config, authSessionRepository)

    @Test
    fun `execute builds an authorize url carrying the PKCE challenge and state`() = runTest {
        val savedSession = slot<TodoistAuthSession>()
        coEvery { authSessionRepository.saveSession(capture(savedSession)) } returns Unit

        val authorizeUrl = interactor.execute().getOrThrow()
        val parameters = authorizeUrl.queryParameters()

        authorizeUrl.substringBefore("?") shouldBe config.authorizeUrl
        parameters["client_id"] shouldBe config.clientId
        parameters["redirect_uri"] shouldBe config.redirectUri
        parameters["scope"] shouldBe config.scope
        parameters["response_type"] shouldBe "code"
        parameters["code_challenge_method"] shouldBe "S256"
        // 保存したverifierから導いたchallengeが載っていること
        parameters["code_challenge"] shouldBe savedSession.captured.codeVerifier.toCodeChallenge().value
        parameters["state"] shouldBe savedSession.captured.state.value
    }

    @Test
    fun `execute never puts the code verifier on the wire`() = runTest {
        val savedSession = slot<TodoistAuthSession>()
        coEvery { authSessionRepository.saveSession(capture(savedSession)) } returns Unit

        val authorizeUrl = interactor.execute().getOrThrow()

        // PKCEの前提。verifierが認可リクエストに漏れるとリダイレクト横取りへの防御にならない
        authorizeUrl shouldNotContain savedSession.captured.codeVerifier.value
    }

    @Test
    fun `execute persists the session before returning so a process death can be survived`() = runTest {
        val savedSession = slot<TodoistAuthSession>()
        coEvery { authSessionRepository.saveSession(capture(savedSession)) } returns Unit

        interactor.execute().getOrThrow()

        savedSession.isCaptured shouldBe true
        CodeVerifier.createSafely(savedSession.captured.codeVerifier.value).shouldNotBeNull()
    }

    @Test
    fun `execute returns a failure when the session cannot be saved`() = runTest {
        coEvery { authSessionRepository.saveSession(any()) } throws IllegalStateException("disk full")

        interactor.execute().isFailure shouldBe true
    }

    private fun String.queryParameters(): Map<String, String> = URI(this).rawQuery.split("&").associate { parameter ->
        val (key, value) = parameter.split("=", limit = 2)
        URLDecoder.decode(key, "UTF-8") to URLDecoder.decode(value, "UTF-8")
    }
}
