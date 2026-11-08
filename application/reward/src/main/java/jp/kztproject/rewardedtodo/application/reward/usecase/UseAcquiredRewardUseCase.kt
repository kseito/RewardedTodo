package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward

interface UseAcquiredRewardUseCase {
    suspend fun execute(id: AcquiredReward.Id): AcquiredReward
}
