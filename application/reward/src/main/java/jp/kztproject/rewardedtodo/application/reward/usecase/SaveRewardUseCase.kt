package jp.kztproject.rewardedtodo.application.reward.usecase

import jp.kztproject.rewardedtodo.domain.reward.RewardInput

interface SaveRewardUseCase {
    suspend fun execute(reward: RewardInput): Result<Unit>
}
