package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.ApiToken
import jp.kztproject.rewardedtodo.domain.todo.AuthorizationCode
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential

/**
 * TodoistのOAuthエンドポイントとのやり取りを担う。
 */
interface ITodoistAuthRepository {

    /** 認可コードをアクセストークンに交換する。公開クライアントのためclient_secretは送らない。 */
    suspend fun exchangeCodeForCredential(
        code: AuthorizationCode,
        codeVerifier: CodeVerifier,
    ): Result<TodoistCredential>

    /** リフレッシュトークンでアクセストークンを再発行する。 */
    suspend fun refreshCredential(refreshToken: RefreshToken): Result<TodoistCredential>

    /** Todoist側でアクセストークンを失効させる。 */
    suspend fun revoke(accessToken: ApiToken): Result<Unit>
}
