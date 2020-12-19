package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.domain.model.Reward
import jp.kztproject.rewardedtodo.reward.domain.model.RewardId

interface GetRewardUseCase {
    suspend fun execute(id: RewardId): Reward?
}