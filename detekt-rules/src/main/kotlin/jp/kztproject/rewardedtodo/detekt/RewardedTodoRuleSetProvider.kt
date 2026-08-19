package jp.kztproject.rewardedtodo.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * このプロジェクト独自のdetektルールセット。
 * `detekt.yml` では `rewardedtodo` というIDで設定する。
 */
class RewardedTodoRuleSetProvider : RuleSetProvider {

    override val ruleSetId: String = "rewardedtodo"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            NoPreviewNameRule(config),
        ),
    )
}
