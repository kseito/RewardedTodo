package jp.kztproject.rewardedtodo.reward.application.usecase

import jp.kztproject.rewardedtodo.domain.reward.Reward

interface DeleteRewardUseCase {
    suspend fun execute(reward: Reward)
}