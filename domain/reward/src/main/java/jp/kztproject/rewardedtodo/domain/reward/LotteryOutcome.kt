package jp.kztproject.rewardedtodo.domain.reward

data class LotteryOutcome(val count: Int, val wonRewards: List<AcquiredReward>, val missCount: Int)
