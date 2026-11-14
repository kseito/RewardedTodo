package jp.kztproject.rewardedtodo.data.reward.network

import jp.kztproject.rewardedtodo.domain.reward.LotteryOutcome

data class LotteryResponse(val count: Int, val results: List<LotteryDrawResponse>) {
    fun domain() = LotteryOutcome(
        count,
        results.mapNotNull {
            it.reward?.domain()
        },
        results.count { it.type == "miss" },
    )
}
