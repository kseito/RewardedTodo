package jp.kztproject.rewardedtodo.domain.todo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ApiTokenTest {

    private val validToken = "0123456789abcdef0123456789abcdef01234567"

    @Test
    fun `create succeeds with exact 40-char lowercase hex token`() {
        val apiToken = ApiToken.create(validToken)
        assertEquals(validToken, apiToken.value)
    }

    @Test
    fun `create trims surrounding whitespace and newlines from pasted input`() {
        val apiToken = ApiToken.create("  $validToken\n")
        assertEquals(validToken, apiToken.value)
    }

    @Test
    fun `create accepts uppercase hex characters`() {
        val upperToken = validToken.uppercase()
        val apiToken = ApiToken.create(upperToken)
        assertEquals(upperToken, apiToken.value)
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
        assertNotNull(apiToken)
        assertEquals(validToken, apiToken?.value)
    }

    @Test
    fun `createSafely returns null for null input`() {
        assertNull(ApiToken.createSafely(null))
    }

    @Test
    fun `createSafely returns null for blank input`() {
        assertNull(ApiToken.createSafely("   "))
    }

    @Test
    fun `createSafely returns null for invalid format`() {
        assertNull(ApiToken.createSafely("not-a-valid-token"))
    }
}
