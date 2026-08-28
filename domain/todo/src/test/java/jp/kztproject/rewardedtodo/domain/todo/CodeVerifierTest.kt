package jp.kztproject.rewardedtodo.domain.todo

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import org.junit.Test

class CodeVerifierTest {

    @Test
    fun `generate produces a verifier within the length range defined by RFC 7636`() {
        val verifier = CodeVerifier.generate()
        verifier.value.length shouldBeGreaterThanOrEqual 43
        verifier.value.length shouldBeLessThanOrEqual 128
    }

    @Test
    fun `generate produces only unreserved characters`() {
        val allowed = (('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('-', '.', '_', '~')).toSet()
        CodeVerifier.generate().value.toSet().subtract(allowed) shouldBe emptySet()
    }

    @Test
    fun `generate produces a different value on every call`() {
        List(10) { CodeVerifier.generate().value }.toSet() shouldHaveSize 10
    }

    @Test
    fun `toCodeChallenge matches the S256 example from RFC 7636 Appendix B`() {
        val verifier = CodeVerifier.create("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")
        verifier.toCodeChallenge().value shouldBe "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create throws when the verifier is shorter than 43 characters`() {
        CodeVerifier.create("a".repeat(42))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create throws when the verifier contains a reserved character`() {
        CodeVerifier.create("a".repeat(42) + "+")
    }

    @Test
    fun `createSafely returns null for an invalid verifier`() {
        CodeVerifier.createSafely("too-short") shouldBe null
        CodeVerifier.createSafely(null) shouldBe null
    }
}
