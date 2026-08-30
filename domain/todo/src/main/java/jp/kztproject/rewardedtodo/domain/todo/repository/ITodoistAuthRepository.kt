package jp.kztproject.rewardedtodo.domain.todo.repository

import jp.kztproject.rewardedtodo.domain.todo.AuthorizationCode
import jp.kztproject.rewardedtodo.domain.todo.CodeVerifier
import jp.kztproject.rewardedtodo.domain.todo.RefreshToken
import jp.kztproject.rewardedtodo.domain.todo.TodoistCredential

/**
 * TodoistのOAuthエンドポイントとのやり取りを担う。
 *
 * 失効(revoke)のAPIは持たない。Todoistの失効エンドポイントは
 * `DELETE /api/v1/access_tokens`・`POST /api/v1/revoke` のいずれも `client_secret` を要求し、
 * 秘密鍵を持たない公開クライアントからは呼び出せないため。
 * 連携解除は端末側のクレデンシャル削除で行い、Todoist側の取り消しはユーザーが
 * Todoistの設定画面から行う。
 */
interface ITodoistAuthRepository {

    /** 認可コードをアクセストークンに交換する。公開クライアントのためclient_secretは送らない。 */
    suspend fun exchangeCodeForCredential(
        code: AuthorizationCode,
        codeVerifier: CodeVerifier,
    ): Result<TodoistCredential>

    /** リフレッシュトークンでアクセストークンを再発行する。 */
    suspend fun refreshCredential(refreshToken: RefreshToken): Result<TodoistCredential>
}
