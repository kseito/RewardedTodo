package jp.kztproject.rewardedtodo.domain.reward

data class BatchLotteryResult(val wonRewards: List<Reward>, val missCount: Int) {
    val hasWon: Boolean get() = wonRewards.isNotEmpty()

    companion object {
        const val DEFAULT_COUNT = 10
    }
}
