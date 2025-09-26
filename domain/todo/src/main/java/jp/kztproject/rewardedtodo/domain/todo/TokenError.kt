package jp.kztproject.rewardedtodo.domain.todo

sealed class TokenError : Exception() {

    data object InvalidFormat : TokenError() {
        private fun readResolve(): Any = InvalidFormat
    }

    data object EmptyToken : TokenError() {
        private fun readResolve(): Any = EmptyToken
        override val message: String = "Token cannot be empty"
    }

    data object AuthenticationFailed : TokenError() {
        private fun readResolve(): Any = AuthenticationFailed
        override val message: String = "Token authentication failed"
    }

    data class NetworkError(override val cause: Throwable) : TokenError() {
        override val message: String = "Network error during token validation: ${cause.message}"
    }

    data object Timeout : TokenError() {
        private fun readResolve(): Any = Timeout
        override val message: String = "Token validation timed out"
    }
}
