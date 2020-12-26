package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.application.model.Result
import jp.kztproject.rewardedtodo.domain.reward.RewardInput

interface SaveRewardUseCase {
    suspend fun execute(reward: RewardInput): Result<Unit>
}