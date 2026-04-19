package jp.kztproject.rewardedtodo.domain.todo

@JvmInline
value class ApiToken private constructor(val value: String) {

    init {
        require(value.isNotBlank()) { "API Token cannot be blank" }
        require(isValidFormat(value)) { "Invalid API Token format" }
    }

    companion object {
        private val TOKEN_REGEX = Regex("[0-9a-fA-F]{40}")

        private fun isValidFormat(token: String): Boolean = token.matches(TOKEN_REGEX)

        /**
         * Creates an ApiToken from a trusted token string.
         * Throws an exception if the token is invalid.
         */
        fun create(token: String): ApiToken = ApiToken(token.trim())

        /**
         * Safely creates an ApiToken from a potentially untrusted token string.
         * Returns null if the token is invalid, avoiding exceptions.
         */
        fun createSafely(token: String?): ApiToken? {
            if (token == null) return null
            val normalized = token.trim()
            if (normalized.isBlank()) return null
            return if (isValidFormat(normalized)) {
                ApiToken(normalized)
            } else {
                null
            }
        }
    }
}
