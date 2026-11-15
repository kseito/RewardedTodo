package jp.kztproject.rewardedtodo.domain.reward

data class AcquiredRewardPage(val items: List<AcquiredReward>, val nextCursor: String?)
