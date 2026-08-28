package jp.kztproject.rewardedtodo.domain.todo

import java.security.SecureRandom
import java.util.Base64

/**
 * CSRF対策のstateパラメータ。
 *
 * 認可リクエスト時に生成し、リダイレクトで戻ってきた値と一致することを確認する。
 * 一致しない場合は第三者が差し込んだ認可コードの可能性があるため交換を行わない。
 */
@JvmInline
value class OAuthState private constructor(val value: String) {

    init {
        require(value.isNotBlank()) { "OAuth state cannot be blank" }
    }

    companion object {
        private const val RANDOM_BYTE_LENGTH = 32

        fun create(value: String): OAuthState = OAuthState(value.trim())

        fun createSafely(value: String?): OAuthState? {
            val normalized = value?.trim() ?: return null
            return if (normalized.isBlank()) null else OAuthState(normalized)
        }

        /** 予測不可能なstateを生成する。 */
        fun generate(): OAuthState {
            val bytes = ByteArray(RANDOM_BYTE_LENGTH).also { SecureRandom().nextBytes(it) }
            return OAuthState(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        }
    }
}
