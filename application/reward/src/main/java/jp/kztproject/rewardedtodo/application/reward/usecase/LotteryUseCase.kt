package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.AcquiredReward

interface LotteryUseCase {
    suspend fun execute(): Result<AcquiredReward?>
}
