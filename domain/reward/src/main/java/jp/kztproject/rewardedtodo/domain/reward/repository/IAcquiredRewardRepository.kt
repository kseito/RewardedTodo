package jp.kztproject.rewardedtodo.domain.reward.repository

import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward
import jp.kztproject.rewardedtodo.domain.reward.AcquiredRewardPage

interface IAcquiredRewardRepository {
    suspend fun findPage(used: Boolean, cursor: String?): AcquiredRewardPage
    suspend fun use(id: AcquiredReward.Id): AcquiredReward
}
