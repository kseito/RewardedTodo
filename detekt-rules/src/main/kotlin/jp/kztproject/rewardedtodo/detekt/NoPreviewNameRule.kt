package jp.kztproject.rewardedtodo.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtAnnotationEntry

/**
 * `@Preview` アノテーションに `name` 引数を付けることを禁止する。
 *
 * このプロジェクトではPreviewを関数名で識別する規約のため、`name` はノイズになり、
 * Showkase + Roborazzi のスクリーンショット名も関数名ベースに揃える方が一貫する。
 *
 * 違反例: `@Preview(name = "Loading")`
 * 正しい例: `@Preview`（状態の違いは関数名 `...LoadingPreview` などで表現する）
 */
class NoPreviewNameRule(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = "NoPreviewName",
        severity = Severity.Style,
        description = "@Preview に name 引数を付けないでください。Preview名は関数名で表現します。",
        debt = Debt.FIVE_MINS,
    )

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotationEntry)

        if (annotationEntry.shortName?.asString() != PREVIEW_ANNOTATION) return

        val hasNameArgument = annotationEntry.valueArguments.any { argument ->
            argument.getArgumentName()?.asName?.asString() == NAME_ARGUMENT
        }
        if (hasNameArgument) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(annotationEntry),
                    message = "@Preview の name 引数は不要です。削除し、Preview名は関数名で表現してください。",
                ),
            )
        }
    }

    private companion object {
        const val PREVIEW_ANNOTATION = "Preview"
        const val NAME_ARGUMENT = "name"
    }
}
