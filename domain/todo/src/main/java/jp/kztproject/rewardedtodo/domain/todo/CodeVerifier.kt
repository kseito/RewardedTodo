package jp.kztproject.rewardedtodo.domain.todo

import java.security.SecureRandom
import java.util.Base64

/**
 * PKCE (RFC 7636) のcode_verifier。
 *
 * client_secretを持てない公開クライアントの代わりの防御で、認可リクエスト時に生成した
 * 値をアプリ内に保持し、トークン交換時に送ってリクエストの出所を証明する。
 * リダイレクトを横取りされても、この値が無ければ認可コードを交換できない。
 */
@JvmInline
value class CodeVerifier private constructor(val value: String) {

    init {
        require(value.length in MIN_LENGTH..MAX_LENGTH) {
            "Code verifier length must be between $MIN_LENGTH and $MAX_LENGTH"
        }
        require(value.all { it in ALLOWED_CHARACTERS }) { "Code verifier contains unreserved characters only" }
    }

    /** S256方式で対応するcode_challengeを導出する。 */
    fun toCodeChallenge(): CodeChallenge = CodeChallenge.s256(this)

    companion object {
        // RFC 7636 Section 4.1
        private const val MIN_LENGTH = 43
        private const val MAX_LENGTH = 128
        private const val RANDOM_BYTE_LENGTH = 32
        private val ALLOWED_CHARACTERS =
            ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '.', '_', '~')

        fun create(value: String): CodeVerifier = CodeVerifier(value)

        fun createSafely(value: String?): CodeVerifier? {
            if (value == null) return null
            return runCatching { CodeVerifier(value) }.getOrNull()
        }

        /** 32バイトの乱数から43文字のcode_verifierを生成する。 */
        fun generate(): CodeVerifier {
            val bytes = ByteArray(RANDOM_BYTE_LENGTH).also { SecureRandom().nextBytes(it) }
            return CodeVerifier(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        }
    }
}
