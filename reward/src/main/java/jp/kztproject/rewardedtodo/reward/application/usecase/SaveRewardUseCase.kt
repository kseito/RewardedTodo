package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.application.model.Result
import jp.kztproject.rewardedtodo.reward.domain.model.RewardInput

interface SaveRewardUseCase {
    suspend fun execute(reward: RewardInput): Result<Unit>
}