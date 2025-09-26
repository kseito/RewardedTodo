package jp.kztproject.rewardedtodo.domain.todo

@JvmInline
value class ApiToken private constructor(val value: String) {

    init {
        require(value.isNotBlank()) { "API Token cannot be blank" }
        require(isValidFormat(value)) { "Invalid API Token format" }
    }

    companion object {
        private fun isValidFormat(token: String): Boolean = token.matches(Regex("[0-9a-f]{40}"))

        /**
         * Creates an ApiToken from a trusted token string.
         * Throws an exception if the token is invalid.
         */
        fun create(token: String): ApiToken = ApiToken(token)

        /**
         * Safely creates an ApiToken from a potentially untrusted token string.
         * Returns null if the token is invalid, avoiding exceptions.
         */
        fun createSafely(token: String?): ApiToken? {
            if (token == null || token.isBlank()) return null
            return if (isValidFormat(token)) {
                ApiToken(token)
            } else {
                null
            }
        }
    }
}
