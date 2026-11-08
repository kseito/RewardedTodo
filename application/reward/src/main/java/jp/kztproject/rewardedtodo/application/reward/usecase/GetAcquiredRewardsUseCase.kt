package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.AcquiredRewardPage

interface GetAcquiredRewardsUseCase {
    suspend fun execute(used: Boolean, cursor: String?): AcquiredRewardPage
}
