package jp.kztproject.rewardedtodo.domain.todo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.Test

class ApiTokenTest {

    private val validToken = "0123456789abcdef0123456789abcdef01234567"

    @Test
    fun `create succeeds with a token string`() {
        val apiToken = ApiToken.create(validToken)
        apiToken.value shouldBe validToken
    }

    @Test
    fun `create trims surrounding whitespace and newlines`() {
        val apiToken = ApiToken.create("  $validToken\n")
        apiToken.value shouldBe validToken
    }

    @Test
    fun `create accepts a token that is not 40-char hex`() {
        // OAuthで発行されるアクセストークンの書式はTodoist側の裁量で変わりうるため、
        // 開発者向けAPIトークンの40桁hexに縛らない
        val opaqueToken = "oauth-issued.token_value~with-unreserved.chars"
        ApiToken.create(opaqueToken).value shouldBe opaqueToken
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create throws when token is blank`() {
        ApiToken.create("   ")
    }

    @Test
    fun `createSafely returns token when input has surrounding whitespace`() {
        val apiToken = ApiToken.createSafely("\t$validToken ")
        apiToken.shouldNotBeNull()
        apiToken.value shouldBe validToken
    }

    @Test
    fun `createSafely returns null for null input`() {
        ApiToken.createSafely(null) shouldBe null
    }

    @Test
    fun `createSafely returns null for blank input`() {
        ApiToken.createSafely("   ") shouldBe null
    }
}
