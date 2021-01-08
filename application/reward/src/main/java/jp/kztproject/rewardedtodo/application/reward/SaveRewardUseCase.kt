package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.application.reward.model.Result
import jp.kztproject.rewardedtodo.domain.reward.RewardInput

interface SaveRewardUseCase {
    suspend fun execute(reward: RewardInput): Result<Unit>
}