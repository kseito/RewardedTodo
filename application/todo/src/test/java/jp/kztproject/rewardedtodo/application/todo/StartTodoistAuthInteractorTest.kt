package jp.kztproject.rewardedtodo.application.todo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import jp.kztproject.rewardedtodo.domain.todo.TodoistOAuthConfig
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
    private val authSessionStore = TodoistAuthSessionStore()
    private val interactor = StartTodoistAuthInteractor(config, authSessionStore)

    @Test
    fun `execute builds an authorize url carrying the PKCE challenge and state`() = runTest {
        val authorizeUrl = interactor.execute().getOrThrow()

        val parameters = authorizeUrl.queryParameters()
        val session = authSessionStore.get().shouldNotBeNull()

        authorizeUrl.substringBefore("?") shouldBe config.authorizeUrl
        parameters["client_id"] shouldBe config.clientId
        parameters["redirect_uri"] shouldBe config.redirectUri
        parameters["scope"] shouldBe config.scope
        parameters["response_type"] shouldBe "code"
        parameters["code_challenge_method"] shouldBe "S256"
        // 保持したverifierから導いたchallengeが載っていること
        parameters["code_challenge"] shouldBe session.codeVerifier.toCodeChallenge().value
        parameters["state"] shouldBe session.state.value
    }

    @Test
    fun `execute never puts the code verifier on the wire`() = runTest {
        val authorizeUrl = interactor.execute().getOrThrow()

        // PKCEの前提。verifierが認可リクエストに漏れるとリダイレクト横取りへの防御にならない
        val session = authSessionStore.get().shouldNotBeNull()
        authorizeUrl shouldNotContain session.codeVerifier.value
    }

    @Test
    fun `execute issues a fresh state and verifier on every call`() = runTest {
        interactor.execute().getOrThrow()
        val first = authSessionStore.get().shouldNotBeNull()

        interactor.execute().getOrThrow()
        val second = authSessionStore.get().shouldNotBeNull()

        // 連携をやり直したとき、古いセッションが残らず上書きされること
        (second.state == first.state) shouldBe false
        (second.codeVerifier == first.codeVerifier) shouldBe false
    }

    private fun String.queryParameters(): Map<String, String> = URI(this).rawQuery.split("&").associate { parameter ->
        val (key, value) = parameter.split("=", limit = 2)
        URLDecoder.decode(key, "UTF-8") to URLDecoder.decode(value, "UTF-8")
    }
}
