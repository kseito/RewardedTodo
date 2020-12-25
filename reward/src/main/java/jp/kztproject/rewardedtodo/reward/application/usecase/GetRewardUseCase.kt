package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.domain.reward.Reward
import jp.kztproject.rewardedtodo.domain.reward.RewardId

interface GetRewardUseCase {
    suspend fun execute(id: RewardId): Reward?
}