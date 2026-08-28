package jp.kztproject.rewardedtodo.domain.todo

/**
 * 認可画面からリダイレクトで返される認可コード。アクセストークンとの交換に一度だけ使える。
 */
@JvmInline
value class AuthorizationCode private constructor(val value: String) {

    companion object {

        fun create(code: String): AuthorizationCode {
            val normalized = code.trim()
            require(normalized.isNotBlank()) { "Authorization Code cannot be blank" }
            return AuthorizationCode(normalized)
        }

        fun createSafely(code: String?): AuthorizationCode? {
            val normalized = code?.trim() ?: return null
            return if (normalized.isBlank()) null else AuthorizationCode(normalized)
        }
    }
}
