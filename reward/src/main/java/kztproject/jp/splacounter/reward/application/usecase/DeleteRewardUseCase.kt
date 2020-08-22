package kztproject.jp.splacounter.reward.application.usecase

import kztproject.jp.splacounter.reward.domain.model.Reward

interface DeleteRewardUseCase {
    suspend fun execute(reward: Reward)
}