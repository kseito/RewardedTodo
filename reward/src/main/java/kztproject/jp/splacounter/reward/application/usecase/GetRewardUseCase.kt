package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward
import kztproject.jp.splacounter.reward.domain.model.RewardId

interface GetRewardUseCase {
    suspend fun execute(id: RewardId): Reward?
}