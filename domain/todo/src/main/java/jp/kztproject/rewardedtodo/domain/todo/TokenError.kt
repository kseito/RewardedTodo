package jp.kztproject.rewardedtodo.domain.todo

sealed class TokenError : Exception() {

    class InvalidFormat : TokenError() {
        override val message: String = "Invalid token format"
    }

    class EmptyToken : TokenError() {
        override val message: String = "Token cannot be empty"
    }

    data class NetworkError(override val cause: Throwable) : TokenError() {
        override val message: String = "Network error during token validation: ${cause.message}"
    }

    class Timeout : TokenError() {
        override val message: String = "Token validation timed out"
    }
}
