package jp.kztproject.rewardedtodo.domain.reward

data class BatchLotteryResult(val wonRewards: List<AcquiredReward>, val missCount: Int) {
    val hasWon: Boolean get() = wonRewards.isNotEmpty()

    companion object {
        const val DEFAULT_COUNT = 10
    }
}
