package jp.kztproject.rewardedtodo.domain.todo

/**
 * 認可画面からリダイレクトで返される認可コード。アクセストークンとの交換に一度だけ使える。
 */
@JvmInline
value class AuthorizationCode private constructor(val value: String) {

    init {
        require(value.isNotBlank()) { "Authorization Code cannot be blank" }
    }

    companion object {

        fun create(code: String): AuthorizationCode = AuthorizationCode(code.trim())

        fun createSafely(code: String?): AuthorizationCode? {
            val normalized = code?.trim() ?: return null
            return if (normalized.isBlank()) null else AuthorizationCode(normalized)
        }
    }
}
