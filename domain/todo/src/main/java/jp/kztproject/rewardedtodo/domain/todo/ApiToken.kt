package jp.kztproject.rewardedtodo.domain.todo

/**
 * Todoistのアクセストークン。
 *
 * かつては開発者向けAPIトークンの40桁hexを前提に検証していたが、OAuthで発行される
 * アクセストークンの書式はTodoist側の裁量で変わりうるため、空でないことのみを検証する。
 */
@JvmInline
value class ApiToken private constructor(val value: String) {

    init {
        require(value.isNotBlank()) { "API Token cannot be blank" }
    }

    companion object {

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
            val normalized = token?.trim() ?: return null
            return if (normalized.isBlank()) null else ApiToken(normalized)
        }
    }
}
