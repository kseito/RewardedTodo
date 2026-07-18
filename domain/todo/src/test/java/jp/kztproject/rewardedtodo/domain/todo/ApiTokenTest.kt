package jp.kztproject.rewardedtodo.domain.todo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.Test

class ApiTokenTest {

    private val validToken = "0123456789abcdef0123456789abcdef01234567"

    @Test
    fun `create succeeds with exact 40-char lowercase hex token`() {
        val apiToken = ApiToken.create(validToken)
        apiToken.value shouldBe validToken
    }

    @Test
    fun `create trims surrounding whitespace and newlines from pasted input`() {
        val apiToken = ApiToken.create("  $validToken\n")
        apiToken.value shouldBe validToken
    }

    @Test
    fun `create accepts uppercase hex characters`() {
        val upperToken = validToken.uppercase()
        val apiToken = ApiToken.create(upperToken)
        apiToken.value shouldBe upperToken
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create throws when token contains non-hex characters`() {
        ApiToken.create("0123456789abcdef0123456789abcdef0123456g")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create throws when token length is not 40`() {
        ApiToken.create("abc")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create throws when token is blank`() {
        ApiToken.create("   ")
    }

    @Test
    fun `createSafely returns token when input has surrounding whitespace`() {
        val apiToken = ApiToken.createSafely("\t$validToken ")
        apiToken.shouldNotBeNull()
        apiToken?.value shouldBe validToken
    }

    @Test
    fun `createSafely returns null for null input`() {
        ApiToken.createSafely(null) shouldBe null
    }

    @Test
    fun `createSafely returns null for blank input`() {
        ApiToken.createSafely("   ") shouldBe null
    }

    @Test
    fun `createSafely returns null for invalid format`() {
        ApiToken.createSafely("not-a-valid-token") shouldBe null
    }
}
