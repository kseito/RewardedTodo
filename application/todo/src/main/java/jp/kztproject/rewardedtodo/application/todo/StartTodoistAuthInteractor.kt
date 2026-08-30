package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.CodeChallenge
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.OAuthState
import jp.kztproject.rewardedtodo.domain.todo.TodoistAuthSession
import jp.kztproject.rewardedtodo.domain.todo.TodoistOAuthConfig
import java.net.URLEncoder
import javax.inject.Inject

class StartTodoistAuthInteractor @Inject constructor(
    private val config: TodoistOAuthConfig,
    private val authSessionStore: TodoistAuthSessionStore,
) : StartTodoistAuthUseCase {

    override suspend fun execute(): Result<String> = runCatching {
        val codeVerifier = CodeVerifier.generate()
        val state = OAuthState.generate()

        // リダイレクトが返るまでメモリ上に保持する
        authSessionStore.save(TodoistAuthSession(state = state, codeVerifier = codeVerifier))

        buildAuthorizeUrl(state, codeVerifier.toCodeChallenge())
    }

    private fun buildAuthorizeUrl(state: OAuthState, codeChallenge: CodeChallenge): String {
        val parameters = linkedMapOf(
            "client_id" to config.clientId,
            "scope" to config.scope,
            "state" to state.value,
            "response_type" to "code",
            "redirect_uri" to config.redirectUri,
            "code_challenge" to codeChallenge.value,
            "code_challenge_method" to CodeChallenge.METHOD_S256,
        )
        val query = parameters.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, Charsets.UTF_8.name())}"
        }
        return "${config.authorizeUrl}?$query"
    }
}
