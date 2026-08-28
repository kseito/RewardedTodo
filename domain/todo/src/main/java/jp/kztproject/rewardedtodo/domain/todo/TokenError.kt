package jp.kztproject.rewardedtodo.domain.todo

sealed class TokenError : Exception() {

    class InvalidFormat : TokenError() {
        override val message: String = "Invalid token format"
    }

    data class NetworkError(override val cause: Throwable) : TokenError() {
        override val message: String = "Network error during token validation: ${cause.message}"
    }

    /** ユーザーが認可画面を閉じた、または認可を拒否した。 */
    class AuthorizationCanceled : TokenError() {
        override val message: String = "Authorization was canceled by the user"
    }

    /** Todoistがエラーを返して認可が完了しなかった（access_denied / invalid_scope など）。 */
    data class AuthorizationFailed(val reason: String) : TokenError() {
        override val message: String = "Authorization failed: $reason"
    }

    /** 返ってきたstateが発行した値と一致しない。第三者が差し込んだ可能性があるため交換しない。 */
    class StateMismatch : TokenError() {
        override val message: String = "OAuth state did not match the issued value"
    }

    /** 認可コードとアクセストークンの交換に失敗した。 */
    data class ExchangeFailed(override val cause: Throwable) : TokenError() {
        override val message: String = "Failed to exchange authorization code: ${cause.message}"
    }

    /** リフレッシュトークンによる再発行に失敗した。再連携が必要になる。 */
    data class RefreshFailed(override val cause: Throwable) : TokenError() {
        override val message: String = "Failed to refresh access token: ${cause.message}"
    }

    /** 端末のブラウザがAuth Tabに対応していない（Chrome 137未満）。 */
    class AuthTabUnsupported : TokenError() {
        override val message: String = "This device's browser does not support Auth Tab"
    }

    /** Todoistと未連携の状態で連携済み前提の操作を行った。 */
    class NotConnected : TokenError() {
        override val message: String = "Todoist is not connected"
    }
}
