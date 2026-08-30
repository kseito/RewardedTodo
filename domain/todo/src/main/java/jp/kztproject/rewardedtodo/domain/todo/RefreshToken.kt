package jp.kztproject.rewardedtodo.domain.todo

/**
 * アクセストークンを再発行するためのリフレッシュトークン。
 *
 * Todoistはリフレッシュのたびに値をローテーションするため、常に最新の値で置き換える。
 */
@JvmInline
value class RefreshToken private constructor(val value: String) {

    companion object {

        fun create(token: String): RefreshToken {
            val normalized = token.trim()
            require(normalized.isNotBlank()) { "Refresh Token cannot be blank" }
            return RefreshToken(normalized)
        }

        fun createSafely(token: String?): RefreshToken? {
            val normalized = token?.trim() ?: return null
            return if (normalized.isBlank()) null else RefreshToken(normalized)
        }
    }
}
