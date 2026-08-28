package jp.kztproject.rewardedtodo.domain.todo

/**
 * 認可リクエストを開始してからリダイレクトが返るまでの間だけ保持する一時的な状態。
 *
 * Auth Tabを開いている間にアプリのプロセスが終了しても認証を継続できるよう永続化する。
 * 認可コードと引き換えに使い切ったら破棄する。
 */
data class TodoistAuthSession(val state: OAuthState, val codeVerifier: CodeVerifier)
