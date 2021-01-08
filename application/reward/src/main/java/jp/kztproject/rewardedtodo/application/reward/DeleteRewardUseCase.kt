package jp.kztproject.rewardedtodo.application.reward

import jp.kztproject.rewardedtodo.domain.reward.Reward

interface DeleteRewardUseCase {
    suspend fun execute(reward: Reward)
}