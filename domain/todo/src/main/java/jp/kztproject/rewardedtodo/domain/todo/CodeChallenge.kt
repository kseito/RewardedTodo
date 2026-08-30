package jp.kztproject.rewardedtodo.domain.todo

import java.security.MessageDigest
import java.util.Base64

/**
 * PKCE (RFC 7636) のcode_challenge。認可リクエストに載せる[CodeVerifier]のハッシュ。
 *
 * Todoistが対応するのはS256方式のみ（plainは非対応）。
 */
@JvmInline
value class CodeChallenge private constructor(val value: String) {

    companion object {
        const val METHOD_S256 = "S256"

        /** code_verifierのASCIIバイト列をSHA-256でハッシュし、base64url(パディング無し)で表す。 */
        fun s256(codeVerifier: CodeVerifier): CodeChallenge {
            val digest = MessageDigest.getInstance("SHA-256")
                .digest(codeVerifier.value.toByteArray(Charsets.US_ASCII))
            return CodeChallenge(Base64.getUrlEncoder().withoutPadding().encodeToString(digest))
        }
    }
}
