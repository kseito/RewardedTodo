package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.reward.domain.model.Reward

interface DeleteRewardUseCase {
    suspend fun execute(reward: Reward)
}