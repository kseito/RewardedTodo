package jp.kztproject.rewardedtodo.domain.todo

import io.kotest.matchers.shouldBe
import org.junit.Test

class TodoistCredentialTest {

    private val accessToken = ApiToken.create("access-token")
    private val refreshToken = RefreshToken.create("refresh-token")

    @Test
    fun `isExpired returns false when the expiry is far ahead`() {
        val credential = TodoistCredential(accessToken, refreshToken, expiresAt = 1_000_000L)
        credential.isExpired(nowMillis = 500_000L) shouldBe false
    }

    @Test
    fun `isExpired returns true within the margin before the expiry`() {
        // 通信の往復中に期限を跨がないよう、期限の60秒前から失効扱いにする
        val credential = TodoistCredential(accessToken, refreshToken, expiresAt = 1_000_000L)
        credential.isExpired(nowMillis = 1_000_000L - TodoistCredential.EXPIRY_MARGIN_MILLIS) shouldBe true
    }

    @Test
    fun `isExpired returns false when the credential has no expiry`() {
        val credential = TodoistCredential(accessToken, refreshToken, expiresAt = null)
        credential.isExpired(nowMillis = Long.MAX_VALUE) shouldBe false
    }

    @Test
    fun `merge keeps the existing refresh token when the refreshed one omits it`() {
        // 消費済みリフレッシュトークンの60秒以内の再試行ではrefresh_tokenが返らない
        val current = TodoistCredential(accessToken, refreshToken, expiresAt = 1_000L)
        val refreshed = TodoistCredential(ApiToken.create("new-access"), refreshToken = null, expiresAt = 2_000L)

        val merged = current.merge(refreshed)

        merged.accessToken.value shouldBe "new-access"
        merged.refreshToken shouldBe refreshToken
        merged.expiresAt shouldBe 2_000L
    }

    @Test
    fun `merge takes the rotated refresh token when the refreshed one provides it`() {
        val current = TodoistCredential(accessToken, refreshToken, expiresAt = 1_000L)
        val rotated = RefreshToken.create("rotated-refresh")
        val refreshed = TodoistCredential(ApiToken.create("new-access"), rotated, expiresAt = 2_000L)

        current.merge(refreshed).refreshToken shouldBe rotated
    }
}
