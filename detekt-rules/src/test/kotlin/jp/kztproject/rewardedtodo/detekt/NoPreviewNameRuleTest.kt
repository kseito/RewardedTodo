package jp.kztproject.rewardedtodo.detekt

import com.google.common.truth.Truth.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.Test

class NoPreviewNameRuleTest {

    @Test
    fun `reports @Preview with a name argument`() {
        val code = """
            annotation class Preview(val name: String = "")

            @Preview(name = "Loading")
            fun LoadingPreview() {}
        """.trimIndent()

        val findings = NoPreviewNameRule().lint(code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report a plain @Preview`() {
        val code = """
            annotation class Preview(val name: String = "", val showBackground: Boolean = false)

            @Preview
            fun PlainPreview() {}

            @Preview(showBackground = true)
            fun BackgroundPreview() {}
        """.trimIndent()

        val findings = NoPreviewNameRule().lint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report a name argument on a non-Preview annotation`() {
        val code = """
            annotation class Named(val name: String = "")

            @Named(name = "something")
            fun Sample() {}
        """.trimIndent()

        val findings = NoPreviewNameRule().lint(code)

        assertThat(findings).isEmpty()
    }
}
