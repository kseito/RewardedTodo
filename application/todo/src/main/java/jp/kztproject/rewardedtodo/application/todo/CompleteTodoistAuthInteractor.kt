package jp.kztproject.rewardedtodo.application.todo

import jp.kztproject.rewardedtodo.domain.todo.AuthorizationCode
import jp.kztproject.rewardedtodo.domain.todo.OAuthState
import jp.kztproject.rewardedtodo.domain.todo.TokenError
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistAuthRepository
import jp.kztproject.rewardedtodo.domain.todo.repository.ITodoistCredentialRepository
import java.net.URI
import java.net.URLDecoder
import javax.inject.Inject

class CompleteTodoistAuthInteractor @Inject constructor(
    private val authRepository: ITodoistAuthRepository,
    private val authSessionStore: TodoistAuthSessionStore,
    private val credentialRepository: ITodoistCredentialRepository,
) : CompleteTodoistAuthUseCase {

    override suspend fun execute(redirectUri: String): Result<Unit> {
        val parameters = runCatching { parseQuery(redirectUri) }
            .getOrElse { return Result.failure(TokenError.AuthorizationFailed("malformed redirect uri")) }

        parameters["error"]?.let { error ->
            authSessionStore.clear()
            return Result.failure(TokenError.AuthorizationFailed(error))
        }

        // stateが一致しない場合は第三者が差し込んだ認可コードの可能性があるため交換しない
        val session = authSessionStore.get()
            ?: return Result.failure(TokenError.StateMismatch())
        val returnedState = OAuthState.createSafely(parameters["state"])
        if (returnedState == null || returnedState != session.state) {
            authSessionStore.clear()
            return Result.failure(TokenError.StateMismatch())
        }

        val code = AuthorizationCode.createSafely(parameters["code"])
            ?: run {
                authSessionStore.clear()
                return Result.failure(TokenError.AuthorizationFailed("authorization code is missing"))
            }

        return authRepository.exchangeCodeForCredential(code, session.codeVerifier)
            .mapCatching { credential ->
                credentialRepository.saveCredential(credential)
                // 認可コードもcode_verifierも使い切りのため、成功したら必ず破棄する
                authSessionStore.clear()
            }
            .recoverCatching { cause ->
                authSessionStore.clear()
                throw if (cause is TokenError) cause else TokenError.ExchangeFailed(cause)
            }
    }

    private fun parseQuery(redirectUri: String): Map<String, String> {
        val query = URI(redirectUri).rawQuery ?: return emptyMap()
        return query.split("&")
            .filter { it.isNotEmpty() }
            .mapNotNull { parameter ->
                val separatorIndex = parameter.indexOf('=')
                if (separatorIndex <= 0) {
                    null
                } else {
                    val key = decode(parameter.substring(0, separatorIndex))
                    val value = decode(parameter.substring(separatorIndex + 1))
                    key to value
                }
            }
            .toMap()
    }

    private fun decode(value: String): String = URLDecoder.decode(value, Charsets.UTF_8.name())
}
